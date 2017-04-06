package netutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by 1 on 17.02.2017.
 */
public class Session implements Runnable {
    private Host _host;
    private Socket _socket;
    private String _name; // имя, сложенное из хоста и порта
    private MessageHandler _msgH;

    public Session(Socket socket, Host host, MessageHandler msgH) {
        this._host = host;
        this._socket = socket;
        this._name = socket.getInetAddress().getHostAddress() + ":" + Integer.toString(socket.getPort());
        this._msgH = msgH;
    }

    public void run() {
        try {
            DataOutputStream dOutputStream;
            try {
                dOutputStream = new DataOutputStream(_socket.getOutputStream());
            } catch (IOException e) {
                _msgH.handleError("The error of getting the output stream.");
                return;
            }
            // чисто для проверки в клиенте: хотят ли с нами работать или нет (здесь: хотят работать)
            try {
                dOutputStream.writeUTF("");
            } catch (IOException e) {
                System.err.println("The connection with waiting Client (" + _name + ") was lost.");
                return;
            }

            System.out.println("[NEW]   The connection with (" + _name + ") was created.");

            String clientMsg = "";
            DataInputStream dInputStream;
            try {
                dInputStream = new DataInputStream(_socket.getInputStream());
            } catch (IOException e) {
                _msgH.handleError("The error of getting the input stream.");
                return;
            }
            while (!clientMsg.equals("exit")) {
                try {
                    clientMsg = dInputStream.readUTF();
                } catch (IOException e) {
                    if (!e.getMessage().equals("Connection reset"))
                        _msgH.handleError("The error of reading from the input stream.");
                    else
                        System.err.println("The connection was reset by Client (" + _name + "). Bye friend!");
                    return;
                }
                _msgH.handle(_name, clientMsg);
            }
            System.out.println("The connection with (" + _name + ") was stopped.");
        }
        finally{
            // уменьшаем счетчик допустимых соединений, так как кто-то завершил работу с сервером
            _host.closeSession();
            try {
                _socket.close();
            } catch (IOException e) {
                _msgH.handleError("The error of closing socket.");
            }
        }
    }

}
