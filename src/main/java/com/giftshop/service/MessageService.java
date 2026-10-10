package com.giftshop.service;

import com.giftshop.model.Message;
import com.giftshop.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> all() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    public void delete(Long id) {
        messageRepository.deleteById(id);
    }

    public long unreadCount() {
        return messageRepository.countByReadFalse();
    }

    public long count() {
        return messageRepository.count();
    }
}
