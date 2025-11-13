package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;

public class DefaultMenuFactory implements MenuFactory {

    private final ServiceManager serviceManager;

    public DefaultMenuFactory(ServiceManager manager) {
        this.serviceManager = manager;
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


    @Override
    public MenuItem createListMechanicsItem() {
        return new MenuItem("Список механиков",
                new ListMechanicsAction(serviceManager), null);
    }

    @Override
    public MenuItem createAddMechanicItem() {
        return new MenuItem("Добавить механика",
                new AddMechanicAction(serviceManager), null);
    }

    @Override
    public MenuItem createRemoveMechanicItem() {
        return new MenuItem("Удалить механика",
                new RemoveMechanicAction(serviceManager), null);
    }


    @Override
    public MenuItem createListGarageSlotsItem() {
        return new MenuItem("Список гаражных мест",
                new ListGarageSlotsAction(serviceManager), null);
    }

    @Override
    public MenuItem createListFreeGarageSlotsNowItem() {
        return new MenuItem("Свободные места (сейчас)",
                new ListFreeGarageSlotsNowAction(serviceManager), null);
    }

    @Override
    public MenuItem createAddGarageSlotItem() {
        return new MenuItem("Добавить гаражное место",
                new AddGarageSlotAction(serviceManager), null);
    }

    @Override
    public MenuItem createRemoveGarageSlotItem() {
        return new MenuItem("Удалить гаражное место",
                new RemoveGarageSlotAction(serviceManager), null);
    }


    @Override
    public MenuItem createFreeCapacityAtItem() {
        return new MenuItem("Свободная ёмкость на дату",
                new FreeCapacityAtAction(serviceManager), null);
    }

    @Override
    public MenuItem createNextFreeDateItem() {
        return new MenuItem("Ближайшая свободная дата",
                new NextFreeDateAction(serviceManager), null);
    }
}
