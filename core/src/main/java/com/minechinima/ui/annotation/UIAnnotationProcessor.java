package com.minechinima.ui.annotation;

import com.google.auto.service.AutoService;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.UIContext;
import com.palantir.javapoet.*;
import org.checkerframework.checker.units.qual.K;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;

@SupportedAnnotationTypes({"com.minechinima.ui.annotation.InGui"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class UIAnnotationProcessor extends AbstractProcessor {
    private Multimap<String, String> services = LinkedListMultimap.create();

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
        Multimap<String, VariableElement> entryPoints = LinkedListMultimap.create();
        Map<String, TypeMirror> entryPointTypes = new LinkedHashMap<>();
        Multimap<String, VariableElement> unnamedEntryPoints = LinkedListMultimap.create();
        Map<String, ParameterizedTypeName> unnamedEntryPointTypes = new LinkedHashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(InGui.class)) {
            TypeElement type = (TypeElement) element.getEnclosingElement();
            TypeMirror entryPointType = getEntryPointType(element, type);

            if (entryPointType == null) {
                unnamedEntryPoints.put(ClassName.get(UnnamedUIHandler.class).simpleName() + type.getSimpleName().toString(), (VariableElement) element);

                if (!unnamedEntryPointTypes.containsKey(ClassName.get(UnnamedUIHandler.class).simpleName() + type.getSimpleName().toString())) {
                    unnamedEntryPointTypes.put(ClassName.get(UnnamedUIHandler.class).simpleName() + type.getSimpleName().toString(), getUnnamedEntryPointType(type));
                }
            } else {
                entryPoints.put(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName(), (VariableElement) element);

                if (!entryPointTypes.containsKey(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName())) {
                    entryPointTypes.put(type.getSimpleName().toString() + processingEnv.getTypeUtils().asElement(entryPointType).getSimpleName(), entryPointType);
                }
            }
        }

        for (String key : entryPoints.keySet()) {
            Multimap<String, String> elementGroups = LinkedListMultimap.create();
            Map<String, TypeMirror> groupTypes = new LinkedHashMap<>();

            try {
                List<FieldSpec> fields = new LinkedList<>();
                TypeMirror generic = ((DeclaredType) processingEnv.getTypeUtils().directSupertypes(entryPointTypes.get(key)).get(1)).getTypeArguments().get(0);

                // Generate fields
                generateFields(entryPoints.get(key), fields, elementGroups, groupTypes);
                /*
                for (VariableElement entryPoint : entryPoints.get(key)) {
                    InGui annotation = entryPoint.getAnnotation(InGui.class);
                    TypeMirror as = null;
                    try {
                        annotation.as();
                    } catch (MirroredTypeException mte) {
                        as = mte.getTypeMirror();
                    }

                    InUIGroup inUIGroup = entryPoint.getAnnotation(InUIGroup.class);
                    if (inUIGroup != null) {
                        elementGroups.put(inUIGroup.key(), entryPoint.getSimpleName().toString());

                        TypeMirror elementGroupAs = null;
                        try {
                            inUIGroup.as();
                        } catch (MirroredTypeException mte) {
                            elementGroupAs = mte.getTypeMirror();
                        }

                        if (groupTypes.containsKey(inUIGroup.key())) {
                            TypeElement groupTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(groupTypes.get(inUIGroup.key()));
                            TypeElement requestedGroupTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(elementGroupAs);
                            if (!groupTypeElement.getQualifiedName().equals(requestedGroupTypeElement.getQualifiedName())) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Group type mismatch: " + groupTypeElement.getQualifiedName() + " != " + requestedGroupTypeElement.getQualifiedName(), entryPoint);
                            }
                        } else {
                            groupTypes.put(inUIGroup.key(), elementGroupAs);
                        }
                    }

                    fields.add(FieldSpec.builder(TypeName.get(as), entryPoint.getSimpleName().toString(), Modifier.PRIVATE, Modifier.FINAL)
                            .initializer("new $T($S)", as, annotation.translationPrefix())
                            .build());
                }
                 */

                TypeMirror renderTarget = entryPointTypes.get(key);
                if (! processingEnv.getTypeUtils().isSubtype(renderTarget, processingEnv.getElementUtils().getTypeElement(BaseStructure.class.getName()).asType())) {
                    renderTarget = generic;
                }

                JavaFile generatedFile = generateRenderClass(key, ClassName.get(renderTarget), ClassName.get(entryPointTypes.get(key)), entryPoints.get(key), elementGroups, groupTypes, fields);

                /*
                for (String groupKey : groupTypes.keySet()) {
                    TypeMirror groupType = groupTypes.get(groupKey);

                    fields.add(FieldSpec.builder(TypeName.get(groupType), groupKey.replaceAll("\\.", ""), Modifier.PRIVATE, Modifier.FINAL)
                            .initializer("new $T($S)", groupType, groupKey)
                            .build());
                }

                 // Generate constructor
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);

                for (Map.Entry<String, String> entry : elementGroups.entries()) {
                    constructorBuilder.addStatement("this.$L.addChild(this.$L)", entry.getKey().replaceAll("\\.", ""), entry.getValue());
                }

                // Generate draw method header
                MethodSpec.Builder drawMethodBuilder = MethodSpec.methodBuilder("draw")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(ClassName.get(UIContext.class), "context").build())
                        .addParameter(ParameterSpec.builder(ClassName.get(generic), "element").build());

                // Generate draw method body for ungrouped elements
                for (VariableElement entryPoint : entryPoints.get(key)) {
                    InGui annotation = entryPoint.getAnnotation(InGui.class);
                    TypeMirror as = null;
                    try {
                        annotation.as();
                    } catch (MirroredTypeException mte) {
                        as = mte.getTypeMirror();
                    }

                    drawMethodBuilder.addStatement("this.$L.init(element.$L)", entryPoint.getSimpleName(), entryPoint.getSimpleName());

                    if (!elementGroups.containsValue(entryPoint.getSimpleName().toString())) {
                        drawMethodBuilder.addStatement("this.$L.render(context)", entryPoint.getSimpleName());
                    }
                }

                // Generate draw method body for grouped elements
                for (String groupKey : groupTypes.keySet()) {
                    drawMethodBuilder.addStatement("this.$L.render(context)", groupKey.replaceAll("\\.", ""));
                }

                TypeMirror renderTarget = entryPointTypes.get(key);
                if (! processingEnv.getTypeUtils().isSubtype(renderTarget, processingEnv.getElementUtils().getTypeElement(BaseStructure.class.getName()).asType())) {
                    renderTarget = generic;
                }

                // Generate getRenderTarget() method
                MethodSpec.Builder renderTargetBuilder = MethodSpec.methodBuilder("getRenderTarget")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), ClassName.get(renderTarget)))
                        .addStatement("return $T.class", renderTarget);


                // Generate type
                TypeSpec typeSpec = TypeSpec.classBuilder(key + "Renderer")
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(entryPointTypes.get(key))
                        .addFields(fields)
                        .addMethod(drawMethodBuilder.build())
                        .addMethod(constructorBuilder.build())
                        .addMethod(renderTargetBuilder.build())
                        .build();

                // Generate file
                JavaFile javaFile = JavaFile.builder("com.minechinima.ui.renderers", typeSpec)
                        .build();

                // Write file
                javaFile.writeTo(processingEnv.getFiler());
                 */

                generateServices(entryPointTypes.get(key), generatedFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (String key : unnamedEntryPointTypes.keySet()) {
            Multimap<String, String> elementGroups = LinkedListMultimap.create();
            Map<String, TypeMirror> groupTypes = new LinkedHashMap<>();

            try {
                List<FieldSpec> fields = new LinkedList<>();

                generateFields(unnamedEntryPoints.get(key), fields, elementGroups, groupTypes);
                ParameterizedTypeName renderTarget = unnamedEntryPointTypes.get(key);

                generateRenderClass(key, renderTarget.typeArguments().get(0), renderTarget, unnamedEntryPoints.get(key), elementGroups, groupTypes, fields);

                services.put(ClassName.get(UnnamedUIHandler.class).canonicalName(), ClassName.get("com.minechinima.ui.renderers", key + "Renderer").canonicalName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generateServices(TypeMirror type, JavaFile generatedFile) {
        if ( ((DeclaredType) type).asElement().toString().equals(Object.class.getCanonicalName())) {
            return;
        }

        Element enclosingElement = processingEnv.getTypeUtils().asElement(type).getEnclosingElement();
        if (enclosingElement != null && enclosingElement instanceof TypeElement enclosingType) {
            services.put(enclosingType.getQualifiedName() + "$" + processingEnv.getTypeUtils().asElement(type).getSimpleName().toString(), getClassName(generatedFile));
        } else {
            services.put(((TypeElement) processingEnv.getTypeUtils().asElement(type)).getQualifiedName().toString(), getClassName(generatedFile));
        }

        if (! ((DeclaredType) type).asElement().toString().equals(IUIHandler.class.getCanonicalName())) {
            processingEnv.getTypeUtils().directSupertypes(type).forEach(superType -> generateServices(superType, generatedFile));
        }
    }

    private String getClassName(JavaFile javaFile) {
        return javaFile.packageName() + "." + javaFile.typeSpec().name();
    }

    private void generateFields(Iterable<VariableElement> elements, List<FieldSpec> fields, Multimap<String, String> elementGroups, Map<String, TypeMirror> groupTypes) {
        for (VariableElement entryPoint : elements) {
            InGui annotation = entryPoint.getAnnotation(InGui.class);
            TypeMirror as = null;
            try {
                annotation.as();
            } catch (MirroredTypeException mte) {
                as = mte.getTypeMirror();
            }

            InUIGroup inUIGroup = entryPoint.getAnnotation(InUIGroup.class);
            if (inUIGroup != null) {
                elementGroups.put(inUIGroup.key(), entryPoint.getSimpleName().toString());

                TypeMirror elementGroupAs = null;
                try {
                    inUIGroup.as();
                } catch (MirroredTypeException mte) {
                    elementGroupAs = mte.getTypeMirror();
                }

                if (groupTypes.containsKey(inUIGroup.key())) {
                    TypeElement groupTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(groupTypes.get(inUIGroup.key()));
                    TypeElement requestedGroupTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(elementGroupAs);
                    if (!groupTypeElement.getQualifiedName().equals(requestedGroupTypeElement.getQualifiedName())) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Group type mismatch: " + groupTypeElement.getQualifiedName() + " != " + requestedGroupTypeElement.getQualifiedName(), entryPoint);
                    }
                } else {
                    groupTypes.put(inUIGroup.key(), elementGroupAs);
                }
            }

            fields.add(FieldSpec.builder(TypeName.get(as), entryPoint.getSimpleName().toString(), Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new $T($S)", as, annotation.translationPrefix())
                    .build());
        }
    }

    private JavaFile generateRenderClass(String key, TypeName renderTarget, TypeName superInterface, Iterable<VariableElement> elements, Multimap<String, String> elementGroups, Map<String, TypeMirror> groupTypes, List<FieldSpec> fields) {
        try {
            for (String groupKey : groupTypes.keySet()) {
                TypeMirror groupType = groupTypes.get(groupKey);

                fields.add(FieldSpec.builder(TypeName.get(groupType), groupKey.replaceAll("\\.", ""), Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T($S)", groupType, groupKey)
                        .build());
            }

            // Generate constructor
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);

            for (Map.Entry<String, String> entry : elementGroups.entries()) {
                constructorBuilder.addStatement("this.$L.addChild(this.$L)", entry.getKey().replaceAll("\\.", ""), entry.getValue());
            }

            // Generate draw method header
            MethodSpec.Builder drawMethodBuilder = MethodSpec.methodBuilder("draw")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(ClassName.get(UIContext.class), "context").build())
                    .addParameter(ParameterSpec.builder(renderTarget, "element").build());

            // Generate draw method body for ungrouped elements
            for (VariableElement entryPoint : elements) {
                InGui annotation = entryPoint.getAnnotation(InGui.class);
                TypeMirror as = null;
                try {
                    annotation.as();
                } catch (MirroredTypeException mte) {
                    as = mte.getTypeMirror();
                }

                drawMethodBuilder.addStatement("this.$L.init(element.$L)", entryPoint.getSimpleName(), entryPoint.getSimpleName());

                if (!elementGroups.containsValue(entryPoint.getSimpleName().toString())) {
                    drawMethodBuilder.addStatement("this.$L.render(context)", entryPoint.getSimpleName());
                }
            }

            // Generate draw method body for grouped elements
            for (String groupKey : groupTypes.keySet()) {
                drawMethodBuilder.addStatement("this.$L.render(context)", groupKey.replaceAll("\\.", ""));
            }

            // Generate getRenderTarget() method
            MethodSpec.Builder renderTargetBuilder = MethodSpec.methodBuilder("getRenderTarget")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(Class.class), renderTarget))
                    .addStatement("return $T.class", renderTarget);


            // Generate type
            TypeSpec typeSpec = TypeSpec.classBuilder(key + "Renderer")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(superInterface)
                    .addFields(fields)
                    .addMethod(drawMethodBuilder.build())
                    .addMethod(constructorBuilder.build())
                    .addMethod(renderTargetBuilder.build())
                    .build();

            // Generate file
            JavaFile javaFile = JavaFile.builder("com.minechinima.ui.renderers", typeSpec)
                    .build();

            // Write file
            javaFile.writeTo(processingEnv.getFiler());

            return javaFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private TypeMirror getEntryPointType(Element element, TypeElement type) {
        Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> optionalEntry =
            element.getAnnotationMirrors().stream()
                .filter(mirror -> processingEnv.getTypeUtils().isSameType(mirror.getAnnotationType(), processingEnv.getElementUtils().getTypeElement(InGui.class.getCanonicalName()).asType()))
                .findFirst()
                .get()
                .getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getSimpleName().toString().equals("entrypoint"))
                .findFirst();

        if (optionalEntry.isEmpty()) {
            return null;
        }

        return (TypeMirror) optionalEntry.get().getValue().getValue();
    }

    private ParameterizedTypeName getUnnamedEntryPointType(TypeElement type) {
        return ParameterizedTypeName.get(ClassName.get(UnnamedUIHandler.class), TypeName.get(type.asType()));
    }
}
