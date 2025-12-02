package autoservice.persistence;

import java.io.*;

/**
 * Менеджер для сохранения и загрузки состояния приложения.
 */
public class StateManager {
    private static final String STATE_FILE = "autoservice_state.ser";
    
    /**
     * Сохраняет состояние приложения в файл.
     * @param state состояние для сохранения
     * @return true если сохранение успешно, false в противном случае
     */
    public boolean saveState(ApplicationState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(state);
            System.out.println("Состояние приложения сохранено в " + STATE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении состояния: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Загружает состояние приложения из файла.
     * @return состояние приложения или null, если загрузка не удалась
     */
    public ApplicationState loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            System.out.println("Файл состояния не найден. Будет использовано начальное состояние.");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            ApplicationState state = (ApplicationState) ois.readObject();
            System.out.println("Состояние приложения загружено из " + STATE_FILE);
            return state;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке состояния: " + e.getMessage());
            return null;
        }
    }
}

