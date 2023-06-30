package gr.uowm.deyakwebapp.views;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import gr.uowm.deyakwebapp.data.entity.Data;
import gr.uowm.deyakwebapp.data.service.CSVExporter;
import gr.uowm.deyakwebapp.data.service.DataService;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Αναλυτικά Δεδομένα")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AllDataView extends Div {

    private Grid<Data> grid;

    private Filters filters;
    private final DataService dataService;
    private final CSVExporter csvExporter;

    private Chart chart = new Chart(ChartType.LINE);
    private Chart secondChart = new Chart(ChartType.LINE);

    private Chart temperatureChart1 = new Chart(ChartType.LINE);
    private Chart temperatureChart2 = new Chart(ChartType.LINE);
    private Configuration configuration = chart.getConfiguration();
    private Configuration secondChartConfiguration = secondChart.getConfiguration();

    private Configuration temperatureChart1Configuration = temperatureChart1.getConfiguration();
    private Configuration temperatureChart2Configuration = temperatureChart2.getConfiguration();

    public AllDataView(DataService dataService, CSVExporter csvExporter) {
        this.dataService = dataService;
        this.csvExporter = csvExporter;
        setSizeFull();
        addClassNames("all-data-view");

        filters = new Filters(this::onSearch, dataService);

        Button exportBtn = new Button("Εξαγωγή σε CSV");
        exportBtn.addClickListener(e -> exportFilteredDataToCSV());
        exportBtn.getStyle().set("padding-right", "30px");
        exportBtn.addClassName("float-right");
        reloadChart();
        reloadChart2();
        reloadChart3();
        reloadChart4();
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, exportBtn,createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }
    private void onSearch() {
        reloadChart();
        reloadChart2();
        reloadChart3();
        reloadChart4();
        refreshGrid();


    }
    private  void reloadChart() {
        chart.drawChart(true);
        chart.getConfiguration().setSeries(new ArrayList<>());

        configuration = chart.getConfiguration();
        configuration.setTitle("Θερμική ενέργεια (E1)");

        XAxis xAxis = configuration.getxAxis();
        xAxis.setTitle("Ημερομηνία");
        xAxis.setType(AxisType.DATETIME);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("MWh");
        configuration = chart.getConfiguration();

        List<Data> filteredData = dataService.getFilteredData(filters);
        Map<Integer, List<Data>> dataByCustomer = groupDataByCustomer(filteredData);
        addSeriesToChart(dataByCustomer);

        chart.drawChart();
    }

    private  void reloadChart2() {
        secondChart.drawChart(true);
        secondChart.getConfiguration().setSeries(new ArrayList<>());

        secondChartConfiguration = secondChart.getConfiguration();
        secondChartConfiguration.setTitle("Παροχή (V1)");
        XAxis secondChartXAxis = secondChartConfiguration.getxAxis();
        secondChartXAxis.setTitle("Ημερομηνία");
        secondChartXAxis.setType(AxisType.DATETIME);
        YAxis secondChartYAxis = secondChartConfiguration.getyAxis();
        secondChartYAxis.setTitle("m³");

        // Group data by customer number
        Map<Integer, List<Data>> dataByCustomer = groupDataByCustomer(dataService.getFilteredData(filters));

        // Add a data series for each customer number to the second chart
        for (Map.Entry<Integer, List<Data>> entry : dataByCustomer.entrySet()) {
            int customerNo = entry.getKey();
            List<Data> customerData = entry.getValue();

            ListDataProvider<Data> dataProvider = new ListDataProvider<>(customerData);
            DataProviderSeries<Data> series = new DataProviderSeries<>(dataProvider, Data::getV1);
            series.setX(Data::getDate);
            series.setY(Data::getV1);
            series.setName("Μετρητής " + customerNo);

            secondChartConfiguration.addSeries(series);
        }

        secondChart.drawChart();
    }

    private  void reloadChart3() {
        temperatureChart1.drawChart(true);
        temperatureChart1.getConfiguration().setSeries(new ArrayList<>());

        temperatureChart1Configuration = temperatureChart1.getConfiguration();
        temperatureChart1Configuration.setTitle("Γράφημα Θερμοκρασίας 1");
        XAxis secondChartXAxis = temperatureChart1Configuration.getxAxis();
        secondChartXAxis.setTitle("Ημερομηνία");
        secondChartXAxis.setType(AxisType.DATETIME);
        YAxis secondChartYAxis = temperatureChart1Configuration.getyAxis();
        secondChartYAxis.setTitle("Θερμοκρασία (°C)");


        // Group data by customer number
        Map<Integer, List<Data>> dataByCustomer = groupDataByCustomer(dataService.getFilteredData(filters));

        // Add a data series for each customer number to the second chart
        for (Map.Entry<Integer, List<Data>> entry : dataByCustomer.entrySet()) {
            int customerNo = entry.getKey();
            List<Data> customerData = entry.getValue();

            ListDataProvider<Data> dataProvider = new ListDataProvider<>(customerData);
            DataProviderSeries<Data> series = new DataProviderSeries<>(dataProvider, Data::getT1);
            series.setX(Data::getDate);
            series.setY(Data::getT1);
            series.setName("Μετρητής " + customerNo);

            temperatureChart1Configuration.addSeries(series);
        }

        temperatureChart1.drawChart();
    }

    private  void reloadChart4() {
        temperatureChart2.drawChart(true);
        temperatureChart2.getConfiguration().setSeries(new ArrayList<>());

        temperatureChart2Configuration = temperatureChart2.getConfiguration();
        temperatureChart2Configuration.setTitle("Γράφημα Θερμοκρασίας 2");
        XAxis secondChartXAxis = temperatureChart2Configuration.getxAxis();
        secondChartXAxis.setTitle("Ημερομηνία");
        secondChartXAxis.setType(AxisType.DATETIME);
        YAxis secondChartYAxis = temperatureChart2Configuration.getyAxis();
        secondChartYAxis.setTitle("Θερμοκρασία (°C)");


        // Group data by customer number
        Map<Integer, List<Data>> dataByCustomer = groupDataByCustomer(dataService.getFilteredData(filters));

        // Add a data series for each customer number to the second chart
        for (Map.Entry<Integer, List<Data>> entry : dataByCustomer.entrySet()) {
            int customerNo = entry.getKey();
            List<Data> customerData = entry.getValue();

            ListDataProvider<Data> dataProvider = new ListDataProvider<>(customerData);
            DataProviderSeries<Data> series = new DataProviderSeries<>(dataProvider, Data::getT2);
            series.setX(Data::getDate);
            series.setY(Data::getT2);
            series.setName("Μετρητής " + customerNo);

            temperatureChart2Configuration.addSeries(series);
        }

        temperatureChart2.drawChart();
    }


    private Map<Integer, List<Data>> groupDataByCustomer(List<Data> filteredData) {
        // Group data by customer number
        Map<Integer, List<Data>> dataByCustomer = new HashMap<>();
        for (Data data : filteredData) {
            int customerNo = data.getCustomerNo();
            if (!dataByCustomer.containsKey(customerNo)) {
                dataByCustomer.put(customerNo, new ArrayList<>());
            }
            dataByCustomer.get(customerNo).add(data);
        }
        return dataByCustomer;
    }

    private void addSeriesToChart(Map<Integer, List<Data>> dataByCustomer) {
        // Add a data series for each customer number
        for (Map.Entry<Integer, List<Data>> entry : dataByCustomer.entrySet()) {
            int customerNo = entry.getKey();
            List<Data> customerData = entry.getValue();

            ListDataProvider<Data> dataProvider = new ListDataProvider<>(customerData);
            DataProviderSeries<Data> series = new DataProviderSeries<>(dataProvider, Data::getE1);
            series.setX(Data::getDate);
            series.setY(Data::getE1);
            series.setName("Μετρητής " + customerNo);

            chart.getConfiguration().addSeries(series);

        }
    }
    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Φίλτρα");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<Data> {

        private final TextField name = new TextField("Κωδικός Μετρητή ή Σειριακός Αριθμός");
        private final DateTimePicker startDate = new DateTimePicker("Ημερομηνία Από");
        private final DateTimePicker endDate = new DateTimePicker("Ημερομηνία Μέχρι");
        private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

        private final Select<Integer> customerNoSelect = new Select<>();
        private DataService dataService;

        public Filters(Runnable onSearch, DataService dataService) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("Κωδικός Μετρητή ή Σειριακός Αριθμός");
            name.setWidth("350x");

            customerNoSelect.setLabel("Κωδικός Μετρητή ή Σειριακός Αριθμός");
            customerNoSelect.setPlaceholder("Επιλέξτε Κωδικό Μετρητή ή Σειριακό Αριθμό");
            customerNoSelect.setItems(dataService.getAllCustomerNumbers());
            customerNoSelect.setWidth("350x");
            customerNoSelect.setEmptySelectionAllowed(true);

            roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Επαναφορά");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                startDate.clear();
                endDate.clear();
                occupations.clear();
                roles.clear();
                onSearch.run();
                customerNoSelect.clear();
            });
            Button searchBtn = new Button("Φιλτράρισμα", new Icon("lumo", "search"));
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(customerNoSelect, createDateRangeFilter(), actions);
        }



        private Component createDateRangeFilter() {
//            startDate.setPlaceholder("Ημερομηνία Από");
//
//            endDate.setPlaceholder("Ημερομηνία Μέχρι");

            // For screen readers
            setAriaLabel(startDate, "Ημερομηνία Από");
            setAriaLabel(endDate, "Ημερομηνία Μέχρι");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        private void setAriaLabel(DateTimePicker datePicker, String label) {
            datePicker.getElement().executeJs("const input = this.inputElement;" //
                    + "input.setAttribute('aria-label', $0);" //
                    + "input.removeAttribute('aria-labelledby');", label);
        }

        @Override
        public Predicate toPredicate(Root<Data> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!customerNoSelect.isEmpty()) {
                Predicate customerNumber = criteriaBuilder.equal(root.get("customerNo"),
                        criteriaBuilder.literal(customerNoSelect.getValue()));
                Predicate serialNo = criteriaBuilder.equal(root.get("serialNo"),
                        criteriaBuilder.literal(customerNoSelect.getValue()));
                predicates.add(criteriaBuilder.or(customerNumber, serialNo));
            }

            if (startDate.getValue() != null) {
                String databaseColumn = "date";
                predicates.add(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.literal(startDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "date";
                predicates.add(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }


            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

    }

    private Component createGrid() {

        grid = new Grid<>(Data.class, false);
        grid.addColumn("customerNo").setAutoWidth(true);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        grid.addColumn(new TextRenderer<>(data -> data.getDate().toString()))
                .setHeader("Ημερομηνία")
                .setAutoWidth(true);
        grid.addColumn(new TextRenderer<>(data -> data.getE1() + " MWh"))
                .setHeader("Heat Energy (E1)")
                .setAutoWidth(true);
        grid.addColumn(new TextRenderer<>(data -> data.getV1() + " m³"))
                .setHeader("Volume (V1)")
                .setAutoWidth(true);

        grid.addColumn(new TextRenderer<>(data -> data.getT1() + " °C"))
                .setHeader("Temperature (T1)")
                .setAutoWidth(true);
        grid.addColumn(new TextRenderer<>(data -> data.getT2() + " °C"))
                .setHeader("Temperature (T2)")
                .setAutoWidth(true);
        grid.setItems(query -> dataService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        HorizontalLayout chartsLayout = new HorizontalLayout(chart, secondChart);
        chartsLayout.setSizeFull();
        chartsLayout.setPadding(false);
        chartsLayout.setSpacing(false);

        HorizontalLayout temperatureChartsLayout = new HorizontalLayout(temperatureChart1, temperatureChart2);
        temperatureChartsLayout.setSizeFull();
        temperatureChartsLayout.setPadding(false);
        temperatureChartsLayout.setSpacing(false);

        // Create a vertical layout to hold the charts and the grid
        VerticalLayout layout = new VerticalLayout(chartsLayout, temperatureChartsLayout, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        return layout;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private void exportFilteredDataToCSV() {
        Map<String, String> filtersMap = new HashMap<>();
        if (filters.customerNoSelect.getValue() != null) {
            filtersMap.put("customerNo", String.valueOf(filters.customerNoSelect.getValue()));
        }
        if (filters.startDate.getValue() != null) {
            filtersMap.put("startDate", filters.startDate.getValue().toString());
        }
        if (filters.endDate.getValue() != null) {
            filtersMap.put("endDate", filters.endDate.getValue().toString());
        }
        String filterParams = filtersMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        Notification.show("Εξαγωγή δεδομένων σε CSV...", 3000, Notification.Position.TOP_END);

        // Open a new tab with the URL including filter parameters
        UI.getCurrent().getPage().executeJs("window.open('/export-csv?" + filterParams + "', '_blank')");    }

}
