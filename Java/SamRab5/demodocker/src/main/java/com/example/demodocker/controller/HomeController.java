@RestController
public class HomeController {

    // Путь для загрузки и выгрузки файлов
    private static final String path = "./files/";

    // Сервис для работы с загрузкой и выгрузкой файлов
    @Autowired
    UploadDownloadService service;

    // Обработчик HTTP POST запроса для загрузки файла
    @PostMapping("/uploadFile")
    public ResponseEntity<List<String>> fileUpload(@RequestParam("file") MultipartFile file)
            throws Exception {
        // Вызываем сервис для загрузки файла
        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
    }

    // Обработчик HTTP GET запроса для скачивания файла
    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@RequestParam String param) throws IOException {
        // Получаем файл из указанного пути
        File file = new File(path + param);
        Path filePath = Paths.get(file.getAbsolutePath());

        // Создаем ресурс из файла
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

        // Настройки заголовков для HTTP ответа
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + param);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        // Возвращаем HTTP ответ с файлом в теле
        return ResponseEntity.ok().headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    // Обработчик HTTP GET запроса для получения списка файлов
    @GetMapping("/getListOfFiles")
    public ResponseEntity<List<String>> getListOfFiles() throws Exception {
        // Вызываем сервис для получения списка файлов
        return new ResponseEntity<>(service.getListofFiles(), HttpStatus.OK);
    }
}
