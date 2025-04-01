- important note: do not cache basestructure objects to use for adjusting/repeating or any other logic. 
BaseStructure instances are not reliable as they might be deleted and re-inserted as new instances via serialization.
Use the UUID / id system to dynamically fetch the objects from the database.