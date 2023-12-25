@Service
public class UploadDownloadService {

    // Путь для загрузки и выгрузки файлов
    private static final String path = "./files";

    // Метод для загрузки файла на сервер
    public List<String> uploadFile(MultipartFile file) throws Exception {
        // Проверяем, не пустое ли имя файла
        if (!file.getOriginalFilename().isEmpty()) {
            // Создаем буферизированный поток для записи файла на сервер
            try (BufferedOutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(new File(path, file.getOriginalFilename())))) {
                // Записываем байты файла в поток
                outputStream.write(file.getBytes());
                // Сбрасываем и закрываем поток
                outputStream.flush();
            }
        } else {
            // Если имя файла пустое, выбрасываем исключение
            throw new Exception("File name is empty");
        }

        // Получаем список файлов в директории и возвращаем его
        return getListofFiles();
    }

    // Метод для получения списка файлов в директории
    public List<String> getListofFiles() throws Exception {
        // Создаем список для хранения имен файлов
        List<String> list = new ArrayList<>();
        // Получаем список файлов в директории
        File files = new File(path);
        String[] fileList = files.list();
        
        // Проверяем, не пустой ли список файлов
        if (fileList != null) {
            // Добавляем имена файлов в список
            Collections.addAll(list, fileList);
        } else {
            // Если список файлов пустой, выбрасываем исключение
            throw new Exception("No files found in the directory");
        }

        // Возвращаем список имен файлов
        return list;
    }
}
