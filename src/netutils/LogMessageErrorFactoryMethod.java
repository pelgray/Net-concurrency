package netutils;

import app.DFLTlogMessageError;

/**
 * Created by 1 on 07.04.2017.
 */
public class LogMessageErrorFactoryMethod {
    public LogMessageErrorWriter getWriter(String name, MessageErrorType type){
        LogMessageErrorWriter writer;
        switch (name) {
            case ("default"):
                writer = new DFLTlogMessageError(type);
                break;
            default:
                writer = null;
                break;
        }

        return writer;
    }
}
