package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;

/**
 * Конкретная реализация фабрики для создания пунктов меню заказов.
 */
public class DefaultOrderMenuFactory implements OrderMenuFactory {
    private final ServiceManager serviceManager;

    public DefaultOrderMenuFactory(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public MenuItem createListOrdersItem() {
        return new MenuItem("Показать все заказы",
                new ListOrdersAction(serviceManager), null);
    }

    @Override
    public MenuItem createListCurrentOrdersItem() {
        return new MenuItem("Показать текущие заказы",
                new ListCurrentOrdersAction(serviceManager), null);
    }

    @Override
    public MenuItem createCreateOrderItem() {
        return new MenuItem("Создать новый заказ",
                new CreatedOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createCloseOrderItem() {
        return new MenuItem("Закрыть заказ",
                new CloseOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createCancelOrderItem() {
        return new MenuItem("Отменить заказ",
                new CancelOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createDeleteOrderItem() {
        return new MenuItem("Удалить заказ",
                new DeleteOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createShiftOrderItem() {
        return new MenuItem("Сместить время заказа",
                new ShiftOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createOrderByMechanicNowItem() {
        return new MenuItem("Заказ выполняемый механиком",
                new OrderByMechanicNowAction(serviceManager), null);
    }

    @Override
    public MenuItem createMechanicByOrderItem() {
        return new MenuItem("Механик выполняющий заказ",
                new MechanicByOrderAction(serviceManager), null);
    }

    @Override
    public MenuItem createOrdersByPeriodItem() {
        return new MenuItem("Заказы за период",
                new OrdersByPeriodAction(serviceManager), null);
    }
}

