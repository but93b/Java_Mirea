@Controller
public class WebController {

    // Обработчик HTTP GET запроса для корневого пути "/"
    @GetMapping("/")
    public String index() {
        // Возвращает имя представления "index", которое будет разрешено
        // соответствующим шаблонизатором представлений (например, Thymeleaf или FreeMarker)
        return "index";
    }
}

