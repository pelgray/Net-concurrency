package concurrentutils;

import java.util.LinkedList;

/**
 * Created by 1 on 17.03.2017.
 */
public class Channel<T> {// здесь очередь незапущенных ждущих клиентов
    // сюда из сервера, отсюда - на диспетчер
    // диспетчер занимается запуском потока

    private final int _maxCount; // максимальное количество сессий, которое мы готовы принять на канал
    private final LinkedList<T> _queue = new LinkedList<>();

    private final Object _lock = new Object();

    public Channel(int maxCount) {
        _maxCount = maxCount;
    }

    public int getSize() {
        synchronized (_lock) {
            return _queue.size();
        }
    }

    public void put(T x){
        synchronized (_lock) {
            while(_queue.size() == _maxCount) {
                try {
                    _lock.wait();
                } catch (InterruptedException e) {
                    System.err.println("Channel: The error of waiting in 'put'-condition.");
                }
            }
            _queue.addLast(x);
            _lock.notifyAll();
        }
    }
    public T take(){
        synchronized (_lock) {
            while (_queue.isEmpty()){
                try {
                    _lock.wait();
                } catch (InterruptedException e) {
                    System.err.println("Channel: The error of waiting in 'take'-condition.");
                }
            }
            _lock.notifyAll();
            return (T)_queue.removeFirst();
        }
    }

}