package com.hood.merch.controllers;

import com.hood.merch.dto.OrderDTO;
import com.hood.merch.dto.ProductDTO;
import com.hood.merch.models.Order;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.OrderRepository;
import com.hood.merch.models.repo.ProductRepository;
import com.hood.merch.service.DefaultEmailService;
import com.hood.merch.service.ProductService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@Controller
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DefaultEmailService emailService;


    @GetMapping("/auth")
    public String auth(Model model) {
        model.addAttribute("title", "Auth");
        return "auth";
    }


    @GetMapping("/pay")
    public String pay(Model model) {
        model.addAttribute("title", "Pay");
        return "payment";
    }


    @GetMapping("/creating-order")
    public String creatingOrder(Model model) {
        model.addAttribute("title", "Оформить заказ");
        return "personal_data";
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная");
        return "home";
    }


    // Добавление товара в корзину.
    @PostMapping("/add-item")
    protected String addItem(HttpServletRequest request,
                             @RequestParam String img,
                             @RequestParam int id,
                             @RequestParam String name,
                             @RequestParam Integer price,
                             @RequestParam String size,
                             @RequestParam Integer quantity,
                             @RequestParam String color,
                             @RequestParam Integer in_stock) throws ServletException, IOException {

        // Проверка наличия товара на складе
        Product stock_check = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        ProductDTO added_item = new ProductDTO(id, name, price, size, quantity, img, color, stock_check.getIn_stock());

        if (stock_check.getIn_stock() >= quantity && stock_check.getSize().getSize().equals(size)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(-1);

            // Получаем корзину из сессии или создаем новую
            ArrayList<ProductDTO> cart = (ArrayList<ProductDTO>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>(); // Инициализация новой корзины
            }

            // Проверка на дубликаты
            for (ProductDTO item : cart) {
                if (item.equals(added_item)) {
                    System.out.println("Найден товар-дубликат " + item.getName());
                    return "redirect:/cart";
                }
            }

            // Добавление товара в корзину
            cart.add(added_item);
            session.setAttribute("cart", cart);
        } else {
            System.out.println("Недостаточно товара на складе для добавления в корзину.");
        }
        return "redirect:/cart";
    }


    // Удаление из корзины единиц товара.
    @PostMapping("/remove-item")
    public String removeItem(HttpServletRequest request, @RequestParam int id) {
        HttpSession session = request.getSession();
        ArrayList<ProductDTO> cart = (ArrayList<ProductDTO>) session.getAttribute("cart");
        cart.removeIf(item -> item.getId() == id);
        if (cart.isEmpty()) {
            session.removeAttribute("cart");
            return "redirect:/cart";
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }


    // Изменение количества единиц товара в корзине.
    @PostMapping("/in-cart")
    public String inCart(HttpServletRequest request,
                         @RequestParam String img,
                         @RequestParam int id,
                         @RequestParam String name,
                         @RequestParam Integer price,
                         @RequestParam String item_size,
                         @RequestParam Integer quantity,
                         @RequestParam String color,
                         @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
        HttpSession session = request.getSession();
        String referrer = request.getHeader("referer");
        ArrayList<ProductDTO> item_in_cart = (ArrayList<ProductDTO>) session.getAttribute("cart");
        ProductDTO added_item = new ProductDTO(id, name, price, item_size, quantity, img, color, in_stock);

        if (stock_check.getIn_stock() >= quantity) {
            for (ProductDTO item : item_in_cart) {
                if (item.equals(added_item)) {
                    item_in_cart.set(item_in_cart.indexOf(item), added_item);
                    session.setAttribute("cart", item_in_cart);
                    return "redirect:/cart";
                }
            }
        } else {
            for (ProductDTO item : item_in_cart) {
                if (item.equals(added_item)) {
                    added_item.setQuantity(stock_check.getIn_stock());
                    item_in_cart.set(item_in_cart.indexOf(item), added_item);
                    session.setAttribute("cart", item_in_cart);
                    return "redirect:/cart";
                }
            }
        }
        return "redirect:/cart";
    }

    /*

    Параметр consumes определяет тип содержимого тела запроса. Например, consumes="application/json" определяет,
    что Content-Type запроса, который отправил клиент должен быть "application/json".
    Можно задать отрицательное указание: consumes="!application/json". Тогда будет требоваться любой Content-Type,
    кроме указанного. Допускается указание нескольких значений: ("text/plain", "application/*).

    Параметр produces определяет формат возвращаемого методом значения. Если на клиенте в header'ах не указан
    заголовок Accept, то не имеет значение, что установлено в produces. Если же заголовок Accept установлен,
    то значение produces должно совпадать с ним для успешного возвращения результата клиенту.
    Параметр produces может также содержать перечисление значений.

    // Запрос на сервер.
    const response = await fetch('/new-order', {
          method: 'POST',
          body: formData,
          headers: {
            'Accept': 'application/json'
          }
    });

    @RequestBody:

    Полученное тело запроса: id=2&id=1&quantity=2&quantity=5&FIO=%D0%A8%D0%BF%D0%B0%D0%BA+%D0%91%D0%BE%D0%B3%D0%B4%
    D0%B0%D0%BD+%D0%90%D0%BD%D0%B4%D1%80%D0%B5%D0%B5%D0%B2%D0%B8%D1%87+&email=bogdanazino777%40gmail.com&tel=%2B7+%28995%29
    +788-81-59&post=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&street=%D0%97%D0%BD%D0%B0%D0%BC%D0%B5%D0%BD%D1%8
    1%D0%BA%D0%B0%D1%8F+19&home=50&country=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D0%B9%D1%81%D0%BA%D0%B0%D1%8F+%D0%A4%D0%B5%D0%B4%
    D0%B5%D1%80%D0%B0%D1%86%D0%B8%D1%8F&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%
    B0&index=141103

    ID: [2, 1]
    Quantity: [2, 5]
    FIO: Шпак Богдан Андреевич
    Email: bogdanazino777@gmail.com
    Телефон: +7 (995) 788-81-59
    Почта: Самовывоз
    Улица: Знаменская 19
    Дом: 50
    Страна: Российская Федерация
    Город: Москва
    Регион: Москва
    Индекс: 141103

    */

    // Новый заказ.
    @RequestMapping(value = "/new-order", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String newOrder(HttpServletRequest request, OrderDTO orderDTO) {

        String address = orderDTO.getCountry() + ", " + orderDTO.getRegion() + ", " + orderDTO.getCity() + ", " + orderDTO.getStreet()
                + ", " + orderDTO.getHome() + ", " + orderDTO.getIndex();

        HttpSession session = request.getSession();

        // Уменьшение кол-ва товаров на складе.
        for (int i = 0; i < orderDTO.getId().size(); i++) {
            Long id = orderDTO.getId().get(i);
            Integer quantity = orderDTO.getQuantity().get(i);

            if (quantity <= 0 || productService.purchaseProduct(id, quantity).equals("Товар закончился")) {
                return "redirect:/cart";
            }
        }

        Integer total_price = 0;
        StringBuilder products = new StringBuilder();

        for (int i = 0; i < orderDTO.getId().size(); i++) {
            Long id = orderDTO.getId().get(i);
            Integer quantity = orderDTO.getQuantity().get(i);

            Product item = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
            total_price += (quantity * item.getPrice());
            products.append(item.getName()).append(", ").append(item.getSize().getSize()).append(", ")
                    .append(quantity).append(", ").append(item.getColor()).append(";").append("\n");
        }

        Date date = new Date();
        String status = "не оплачено";
        Order order = new Order(products.toString(), orderDTO.getFIO(), orderDTO.getEmail(), orderDTO.getTel(), orderDTO.getPost(),
                address, status, total_price, date);
        orderRepository.save(order);
        System.out.println(products);
        System.out.println(address);

        try {
            emailService.sendEmailWithAttachment(orderDTO.getEmail(), "Оформление заказа", "Спасибо, что выбрали нас \uD83E\uDD70 \n"
                            + products + address + "\nСкоро вам поступит звонок от менеджера",
                    "classpath:templates/post.html");
        } catch (MessagingException | FileNotFoundException mailException) {
            System.out.println("Unable to send email");
        }

        session.removeAttribute("cart");

        // Возвращаем ответ клиенту
        return "redirect:/pay";
    }


    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("title", "Корзина");
        return "cart";
    }


    @GetMapping("/scarf")
    public String scarf(Model model) {
        Product product = productRepository.findById(1).orElseThrow(() -> new RuntimeException("Товар не найден"));
        String size = product.getSize().getSize();
        model.addAttribute("product", Collections.singletonList(product));
        model.addAttribute("size", size);
        return "scarf";
    }


//    @RequestMapping(value = "/updateSize", method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String updateSize(HttpServletRequest request, Model model, @RequestParam Integer id, @RequestParam String model_name) {
//        // Обновление размера продукта
//        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
//
//        HttpSession session = request.getSession();
//        session.setAttribute("oversizeId", id);
//
//        ArrayList<Product> res = new ArrayList<>();
//        res.add(product);
//        model.addAttribute(model_name, res);
//        return "redirect:/oversize";
//    }


//    @GetMapping(value = "/oversize/{id}")
//    public String oversize(Model model, @PathVariable("id") Integer id) {
//
//        if (id == null) {
//            // Если ID не найден, можно вернуть ошибку или загрузить продукт по умолчанию
//            id = 2; // Загрузка продукта по умолчанию, если ID не установлен
//        }
//
//        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
//        model.addAttribute("product1", Collections.singletonList(product));
//
//        return "oversize";
//    }


    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Контакты");
        return "contacts";
    }
}
