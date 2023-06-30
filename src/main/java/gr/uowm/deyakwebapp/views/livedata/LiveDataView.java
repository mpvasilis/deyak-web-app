package gr.uowm.deyakwebapp.views.livedata;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Page;
import gr.uowm.deyakwebapp.data.entity.Data;
import gr.uowm.deyakwebapp.data.service.DataService;
import gr.uowm.deyakwebapp.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@PageTitle("Live Data")
@Route(value = "live-data", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class LiveDataView extends Composite<VerticalLayout> {

    private HorizontalLayout layoutRow = new HorizontalLayout();

    private Select select = new Select();

    private HorizontalLayout layoutRow2 = new HorizontalLayout();

    private VerticalLayout layoutColumn2 = new VerticalLayout();

    private H3 h3 = new H3();

    private H4 h4 = new H4();

    private VerticalLayout layoutColumn3 = new VerticalLayout();

    private H3 h32 = new H3();

    private H4 h42 = new H4();

    private VerticalLayout layoutColumn4 = new VerticalLayout();

    private H3 h33 = new H3();

    private H4 h43 = new H4();

    private HorizontalLayout layoutRow3 = new HorizontalLayout();

    private VerticalLayout layoutColumn5 = new VerticalLayout();

    private H3 h34 = new H3();

    private H4 h44 = new H4();

    private VerticalLayout layoutColumn6 = new VerticalLayout();

    private H3 h35 = new H3();

    private H4 h45 = new H4();

    private VerticalLayout layoutColumn7 = new VerticalLayout();

    private H3 h36 = new H3();

    private H4 h46 = new H4();

    private HorizontalLayout layoutRow4 = new HorizontalLayout();
    private DataService dataService;


    public LiveDataView(DataService dataService) {
        this.dataService = dataService;
        getContent().setHeightFull();
        getContent().setWidthFull();
        layoutRow.setWidthFull();
        layoutRow.addClassName(Gap.MEDIUM);
        select.setLabel("Κωδικός Μετρητή");
        select.setItems(dataService.getAllCustomerNumbers());
        select.addValueChangeListener(event -> {
            String selectedCustomerNumber = String.valueOf(event.getValue());
            int customerNumber = Integer.parseInt(selectedCustomerNumber);
            updateDataForCustomer();
        });
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth(null);
        h3.setText("Heat Energy (E1)");
        h4.setText("Loading...");
        layoutColumn3.setHeightFull();
        layoutRow2.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth(null);
        h32.setText("Volume V1");
        h42.setText("Loading...");
        layoutColumn4.setHeightFull();
        layoutRow2.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth(null);
        h33.setText("t1 actual");
        h43.setText("Loading...");
        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutColumn5.setHeightFull();
        layoutRow3.setFlexGrow(1.0, layoutColumn5);
        layoutColumn5.setWidth(null);
        h34.setText("t2 actual");
        h44.setText("Loading...");
        layoutColumn6.setHeightFull();
        layoutRow3.setFlexGrow(1.0, layoutColumn6);
        layoutColumn6.setWidth(null);
        h35.setText("Info Code");
        h45.setText("Loading...");
        layoutColumn7.setHeightFull();
        layoutRow3.setFlexGrow(1.0, layoutColumn7);
        layoutColumn7.setWidth(null);
        h36.setText("Operating Hours");
        h46.setText("Loading...");
        layoutRow4.setWidthFull();
        layoutRow4.addClassName(Gap.MEDIUM);
        getContent().add(layoutRow);
        layoutRow.add(select);
        getContent().add(layoutRow2);
        layoutRow2.add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(h4);
        layoutRow2.add(layoutColumn3);
        layoutColumn3.add(h32);
        layoutColumn3.add(h42);
        layoutRow2.add(layoutColumn4);
        layoutColumn4.add(h33);
        layoutColumn4.add(h43);
        getContent().add(layoutRow3);
        layoutRow3.add(layoutColumn5);
        layoutColumn5.add(h34);
        layoutColumn5.add(h44);
        layoutRow3.add(layoutColumn6);
        layoutColumn6.add(h35);
        layoutColumn6.add(h45);
        layoutRow3.add(layoutColumn7);
        layoutColumn7.add(h36);
        layoutColumn7.add(h46);
        getContent().add(layoutRow4);
        UI ui = UI.getCurrent();
        Page page = ui.getPage();
        ui.setPollInterval(10000);
        AtomicBoolean uiActive = new AtomicBoolean(true);
        ui.addPollListener(e -> {
            if (uiActive.get()) {
                updateDataForCustomer();
            }
        });

        ui.addBeforeLeaveListener(e -> uiActive.set(false));

    }

    private void updateDataForCustomer() {
        Integer selectedCustomerNumber = (Integer) select.getValue();
        if (selectedCustomerNumber != null) {
            Optional<Data> lastData = dataService.getLastDataForCustomer(selectedCustomerNumber);
            if (lastData.isPresent()) {
                Data data = lastData.get();
                UI.getCurrent().accessSynchronously(() -> {
                    Notification.show("Data updated for customer " + selectedCustomerNumber, 1000, Notification.Position.TOP_CENTER);
                    h4.setText(String.valueOf(data.getE1())+ " kWh");
                    h42.setText(String.valueOf(data.getV1())+ " m3");
                    h43.setText(String.valueOf(data.getT1())+ " °C");
                    h44.setText(String.valueOf(data.getT2())+ " °C");
                    h45.setText(String.valueOf(data.getInfoCode()));
                    h46.setText(String.valueOf(data.getOperatingHours()));
                });
            } else {
                UI.getCurrent().accessSynchronously(() -> {
                    h4.setText("N/A");
                    h42.setText("N/A");
                    h43.setText("N/A");
                    h44.setText("N/A");
                    h45.setText("N/A");
                    h46.setText("N/A");
                });
            }
        }
    }


}
