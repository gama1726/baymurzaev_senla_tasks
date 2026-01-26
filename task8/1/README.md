# Электронный администратор автосервиса

## Структура проекта

Проект использует модульную структуру Maven:

```
autoservice-parent/
├── pom.xml (parent)
├── checkstyle.xml
├── autoservice-core/          # Основной модуль
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/          # Исходный код
│       │   └── resources/     # Ресурсы (config.properties, log4j2.xml, SQL скрипты)
│       └── test/
└── autoservice-app/           # Модуль приложения
    ├── pom.xml
    └── src/
        └── main/
            ├── java/          # Точка входа (App.java)
            └── resources/
```

## Сборка проекта

### Требования
- Java 17+
- Maven 3.6+

### Команды сборки

```bash
# Сборка всех модулей
mvn clean install

# Сборка с проверкой стиля кода (checkstyle)
mvn clean validate

# Запуск приложения через Maven
# ВАЖНО: Запускайте из корня проекта (где находится родительский pom.xml)!

# Вариант 1: Сначала собрать, потом запустить
mvn clean install -DskipTests
mvn exec:java -pl autoservice-app

# Вариант 2: Одной командой (соберет зависимости автоматически)
mvn clean install -DskipTests exec:java -pl autoservice-app

# Вариант 3: Если Maven кэшировал ошибку, очистите кэш
mvn dependency:purge-local-repository -DmanualInclude="autoservice:autoservice-core"
mvn clean install -DskipTests
mvn exec:java -pl autoservice-app
```

### Запуск в IntelliJ IDEA

**Через Maven панель:**
1. Откройте Maven панель: `View` → `Tool Windows` → `Maven` (или `Alt + 1`)
2. Найдите `autoservice-app` → `Plugins` → `exec` → `exec:java`
3. Дважды кликните на `exec:java`

**Через Run Configuration:**
1. Откройте `autoservice-app/src/main/java/autoservice/App.java`
2. Правый клик → `Run 'App.main()'`
3. Или нажмите зеленую стрелку рядом с методом `main`

## Логирование

Проект использует Log4J2 для логирования. Конфигурация находится в:
- `autoservice-core/src/main/resources/log4j2.xml`

Логи записываются в:
- Консоль (уровень INFO)
- Файл `logs/autoservice.log`

## Проверка стиля кода

Проект использует maven-checkstyle-plugin для проверки стиля кода.
Конфигурация находится в `checkstyle.xml` в корне проекта.

Плагин автоматически запускается на фазе `validate`.

## База данных

Проект использует H2 Database. Настройки подключения в `config.properties`.

При первом запуске автоматически создается структура БД и заполняются тестовые данные.
