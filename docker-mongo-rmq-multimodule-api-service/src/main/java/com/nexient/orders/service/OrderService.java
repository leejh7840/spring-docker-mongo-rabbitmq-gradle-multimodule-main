package com.nexient.orders.service;

import com.nexient.orders.data.config.MessagingConfig;
import com.nexient.orders.data.entity.Order;
import com.nexient.orders.data.entity.OrderStatus;
import com.nexient.orders.data.repository.OrderRepository;
import lombok.AllArgsConstructor;
import net.jazdw.rql.parser.ASTNode;
import net.jazdw.rql.parser.RQLParser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@EnableMongoRepositories(basePackages = {"com.nexient.orders.data.repository"})
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private RabbitTemplate rabbitTemplate;

    private MongoTemplate mongoTemplate;

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    public String saveOrder(Order order, String restaurantName) {

        OrderStatus orderStatus = new OrderStatus(order, "PROCESS",
                "Order successfully placed in " + restaurantName);
        rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, orderStatus);

        orderRepository.save(order);

        return "SUCCESS!";
    }

    public String updateOrder(Order order, String id) {
        orderRepository.save(order);
        return "SUCCESS!";
    }

    public Optional<Order> getOrder(String id) {
        return orderRepository.findById(id);
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    public void deleteByPriceGreaterThan(String price) {
        orderRepository.deleteByPriceGreaterThan(Double.valueOf(price));
    }

    public Optional<Order> getOrderByItem(String item) {
        return orderRepository.findByItem(item);
    }

    public List<Order> getOrderByQtyGreaterThan(String qty) {
        return orderRepository.findByQtyGreaterThan(qty);
    }

    public List<Order> getOrderByQtyLessThan(String qty) {
        return orderRepository.findByQtyLessThan(qty);
    }

    public List<Order> getOrderByPriceGreaterThan(String price) {
        return orderRepository.findByPriceGreaterThan(price);
    }

    public List<Order> getOrderByPriceLessThan(String price) {
        return orderRepository.findByPriceLessThan(price);
    }

    public List<Order> getOrdersByPriceBetween(String price1, String price2) {
        return orderRepository.getOrdersByPriceBetween(price1, price2);
    }

    public List<Order> getOrdersByQtyBetween(String qty1, String qty2) {
        return orderRepository.getOrdersByQtyBetween(qty1, qty2);
    }

    public List<Order> getAllOrderPriceSorted() {
        return orderRepository.findAll(Sort.by(Direction.ASC, "price"));
    }

    public List<Order> getAllOrderItemSorted() {
        return orderRepository.findAll(Sort.by(Direction.ASC, "item"));
    }

    public List<Order> getAllOrderQtySorted() {
        return orderRepository.findAll(Sort.by(Direction.ASC, "qty"));
    }

    public List<Order> findOrderDynamic(String queryString) {
        if (queryString == null || "".equalsIgnoreCase(queryString)) {
            return null;
        }

        RQLParser parser = new RQLParser();
        ASTNode node = parser.parse(queryString);

        Criteria criteria = buildDynamicCriteria(node);
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Order.class);
    }

    /**
     *
     * @param node
     * @return Criteria
     *
     * Accepts an ASTNode object and parses through recursively
     * to build Mongo Criteria
     */
    public Criteria buildDynamicCriteria(ASTNode node) {

        if (node == null) {
            return null;
        }
        List<Criteria> childrenCriteria = new ArrayList<>();
        List<Object> arguments = node.getArguments();

        // If Node is logical operator, traverse every child node
        // If comparator node, build criteria from terminal child nodes
        if ("and".equalsIgnoreCase(node.getName()) || "or".equalsIgnoreCase(node.getName())) {

            for (Object argumentObject : arguments) {
                ASTNode argumentNode = (ASTNode) argumentObject;
                childrenCriteria.add(buildDynamicCriteria(argumentNode));
            }

            if ("and".equalsIgnoreCase(node.getName())) {
                return new Criteria().andOperator(childrenCriteria.toArray(new Criteria[childrenCriteria.size()]));
            }
            if ("or".equalsIgnoreCase(node.getName())) {
                return new Criteria().orOperator(childrenCriteria.toArray(new Criteria[childrenCriteria.size()]));
            }
        } else {
            String key = arguments.get(0).toString();
            String value = arguments.get(1).toString();
            String operator = node.getName();

            return buildCriteria(key, Arrays.asList(value,operator));
        }

        return null;
    }

    public Criteria buildCriteria(String key, List<String>valAndOper) {

        if (key == null || "".equalsIgnoreCase(key)) {
            return null;
        }
        if (valAndOper == null) {
            return null;
        }
        if (valAndOper.size() != 2) {
            return null;
        }
        String val = valAndOper.get(0);
        String operator = valAndOper.get(1);
        if (val == null || "".equalsIgnoreCase(val)) {
            return null;
        }
        if (operator == null || "".equalsIgnoreCase(operator)) {
            return null;
        }

        Criteria criteria = new Criteria();

        switch (operator) {
            case "eq":
                criteria = Criteria.where(key).is(val);
                break;
            case "lt":
                criteria = Criteria.where(key).lt(val);
            case "lte":
                criteria = Criteria.where(key).lte(val);
                break;
            case "gt":
                criteria = Criteria.where(key).gt(val);
                break;
            case "gte":
                criteria = Criteria.where(key).gte(val);
                break;
        }

        return criteria;
    }

}
