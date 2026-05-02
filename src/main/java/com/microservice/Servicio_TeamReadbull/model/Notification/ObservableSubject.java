package com.microservice.Servicio_TeamReadbull.model.Notification;

public interface ObservableSubject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifyObservers();
    void update();
}
