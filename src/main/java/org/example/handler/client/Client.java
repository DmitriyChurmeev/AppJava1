package org.example.handler.client;

import org.example.handler.response.Response;

public interface Client {

    /**
     * блокирующий вызов сервиса 1 для получения статуса заявки
     * @param id
     * @return
     */
    Response getApplicationStatus1(String id);

    /**
     * блокирующий вызов сервиса 2 для получения статуса заявки
     * @param id
     * @return
     */
    Response getApplicationStatus2(String id);
}
