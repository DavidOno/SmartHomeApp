package de.smarthome.app.utility;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Utility to create Toasts globally.
 * Reason: All interactions with a database may result in a failure,
 * but passing the necessary information to notify the user as parameter every time,
 * makes the code less readable and cumbersome.
 * Since it is relevant for all database-interactions,
 * I think it's an appropriate and justified solution to make this functionality global.
 */
public class ToastUtility {

    private static ToastUtility instance;
    private MutableLiveData<Boolean> isToastNew = new MutableLiveData<>(false);
    private String message;

    /**
     * Private constructor should enforce the singleton-pattern.
     */
    private ToastUtility() {
        //empty constructor
    }

    /**
     * Ensures that only a single instance is in use.
     *
     * @return Returns an instance of the ToastUtility.
     */
    public static ToastUtility getInstance() {
        if (instance == null) {
            instance = new ToastUtility();
        }
        return instance;
    }

    /**
     * Toast is getting prepared.
     *
     * @param message The message to display via Toast.
     */
    public void prepareToast(String message) {
        this.message = message;
        isToastNew.postValue(true);
    }

    public LiveData<Boolean> getNewToast() {
        return isToastNew;
    }

    /**
     * Returns the Message.
     *
     * @return Returns the message, which should be displayed via Toast.
     */
    public String getMessage() {
        String result = message;
        isToastNew.postValue(false);
        return result;
    }
}