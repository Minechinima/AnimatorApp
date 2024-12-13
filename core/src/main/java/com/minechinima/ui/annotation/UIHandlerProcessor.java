package com.minechinima.ui.annotation;

import com.google.auto.service.AutoService;
import com.google.common.collect.ArrayListMultimap;
import com.minechinima.ui.UIContext;
import com.palantir.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@SupportedAnnotationTypes("com.minechinima.ui.annotation.InGui")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class UIHandlerProcessor extends AbstractProcessor {
    private ArrayListMultimap<String, String> services = ArrayListMultimap.create();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            generateServiceFiles();
        } else {
            generateEntryPointClasses(annotations, roundEnv);
        }

        return true;
    }

    private void generateEntryPointClasses(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ArrayListMultimap<String, VariableElement> entryPoints = ArrayListMultimap.create();
        HashMap<String, TypeMirror> entryPointTypes = new HashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(InGui.class)) {
            TypeElement type = (TypeElement) element.getEnclosingElement();
            TypeMirror entryPointType = getEntryPointType(element);
            entryPoints.put(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName(), (VariableElement) element);

            if (!entryPointTypes.containsKey(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName())) {
                entryPointTypes.put(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName(), entryPointType);
            }
        }

        for (String key : entryPoints.keySet()) {
            try {
                ArrayList<FieldSpec> fields = new ArrayList<>();
                TypeMirror generic = ((DeclaredType) processingEnv.getTypeUtils().directSupertypes(entryPointTypes.get(key)).get(1)).getTypeArguments().get(0);

                // Generate fields
                for (VariableElement entryPoint : entryPoints.get(key)) {
                    InGui annotation = entryPoint.getAnnotation(InGui.class);
                    TypeMirror as = null;
                    try {
                        annotation.as();
                    } catch (MirroredTypeException mte) {
                        as = mte.getTypeMirror();
                    }

                    fields.add(FieldSpec.builder(TypeName.get(as), entryPoint.getSimpleName().toString(), Modifier.PRIVATE, Modifier.FINAL)
                            .initializer("new $T($S)", as, annotation.translationPrefix())
                            .build());
                }

                // Generate draw method header
                MethodSpec.Builder drawMethodBuilder = MethodSpec.methodBuilder("draw")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(ClassName.get(UIContext.class), "context").build())
                        .addParameter(ParameterSpec.builder(ClassName.get(generic), "element").build());

                // Generate draw method body
                for (VariableElement entryPoint : entryPoints.get(key)) {
                    InGui annotation = entryPoint.getAnnotation(InGui.class);
                    TypeMirror as = null;
                    try {
                        annotation.as();
                    } catch (MirroredTypeException mte) {
                        as = mte.getTypeMirror();
                    }

                    drawMethodBuilder.addStatement("this.$L.init(element.$L).render(context)", entryPoint.getSimpleName(), entryPoint.getSimpleName());
                }

                // Generate type
                TypeSpec typeSpec = TypeSpec.classBuilder(key + "Renderer")
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(entryPointTypes.get(key))
                        .addFields(fields)
                        .addMethod(drawMethodBuilder.build())
                        .build();

                // Generate file
                JavaFile javaFile = JavaFile.builder("com.minechinima.ui.renderers", typeSpec)
                        .build();

                // Write file
                javaFile.writeTo(processingEnv.getFiler());

                Element enclosingElement = processingEnv.getTypeUtils().asElement(entryPointTypes.get(key)).getEnclosingElement();
                if (enclosingElement != null && enclosingElement instanceof TypeElement enclosingType) {
                    services.put(enclosingType.getQualifiedName() + "$" + processingEnv.getTypeUtils().asElement(entryPointTypes.get(key)).getSimpleName().toString(), ClassName.get("com.minechinima.ui.renderers", key + "Renderer").canonicalName());
                } else {
                    services.put(((TypeElement) processingEnv.getTypeUtils().asElement(entryPointTypes.get(key))).getQualifiedName().toString(), ClassName.get("com.minechinima.ui.renderers", key + "Renderer").canonicalName());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generateServiceFiles() {
        for (String key : services.keySet()) {
            String serviceFileName = "META-INF/services/" + key;

            FileObject serviceFile = null;
            try {
                serviceFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", serviceFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (Writer writer = serviceFile.openWriter()) {
                for (String service : services.get(key)) {
                    writer.write(service);
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TypeMirror getEntryPointType(Element element) {
        return (TypeMirror) element.getAnnotationMirrors().get(0).getElementValues().entrySet().stream().findFirst().get().getValue().getValue();
    }
}
