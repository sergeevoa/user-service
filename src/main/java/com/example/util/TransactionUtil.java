package com.example.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

/*Класс, содержащий обобщенный утилитный метод для безопасной работы с любыми транзакциями*/
public class TransactionUtil {
    private final SessionFactory sessionFactory;

    public TransactionUtil(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <R> R withTransaction(Function<Session, R> block) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = block.apply(session);    //Выполняем код переданной функции
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                System.out.println("Transaction rolled back due to error.");
            }
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
