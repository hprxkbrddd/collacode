package com.collacode.document.controller;

import com.collacode.document.crdt.CrdtOperation;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

public class DocumentUpdateController {
    /**
     * Обрабатывает сообщения об обновлении документа и рассылает их всем подписчикам
     * @param update Объект с изменениями документа
     * @param docId ID документа из URL
     * @return Возвращает то же обновление для рассылки всем подписчикам
     *
     * Пример клиентского запроса:
     * stompClient.send("/app/document/123/update", {}, JSON.stringify(update));
     */
    @MessageMapping("/document/{docId}/update")  // Обрабатывает сообщения, отправленные на "/app/document/{docId}/update"
    @SendTo("/topic/document/{docId}")  // Результат метода отправляется всем подписчикам "/topic/document/{docId}"
    public CrdtOperation handleUpdate(
            @Payload CrdtOperation update,  // Тело STOMP сообщения
            @DestinationVariable String docId  // Переменная из URL
    ) {
        // Здесь можно добавить логику валидации или обработки перед рассылкой
        return update;  // Рассылаем полученное обновление всем подписчикам
    }
}
