package com.minechinima.controller;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.exceptions.UserException;

public class DatabaseController {
    public static void deleteObject(BBDatabase database, BaseObject object) throws UserException {
        if (!database.objects.contains(object)) {
            throw new UserException("The database does not contain the specified object.");
        }

        database.objects.remove(object);

        if (database.objects.contains(object)) {
            throw new UserException("The object could not be deleted from the database.");
        }
    }
}
