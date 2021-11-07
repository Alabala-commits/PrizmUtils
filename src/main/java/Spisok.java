import javax.swing.*;
import java.util.Set;

public class Spisok extends JList<String> {

    private final DefaultListModel<String> model;

    private String selectedAddress;
    private Set<String> listAddresses;
    private ListWindow listWindow;

//=========================================================

    public Spisok(Set<String> addresses, ListWindow listWindow) {
        this(new DefaultListModel<>());
        addElements(addresses);
        listAddresses = addresses;
        this.listWindow = listWindow;
    }

    public Spisok(DefaultListModel<String> model) {
        super(model);
        this.model = model;
        prepareViewDetails();
    }

    void addElement(String address) {
        if ( ! listAddresses.add(address)) {
            listWindow.alert("Адрес\n" + address + "\nуже есть в списке");
            return;
        }
        update();
    }

    void removeElement(String address) {
        if (address != null) {
            listAddresses.remove(address);
            update();
        }
    }

    void addElements(Set<String> addresses) {
        if (addresses != null) {
            addresses.forEach(model::addElement);
        }
    }

    private void update() {
        model.clear();
        addElements(listAddresses);
        listWindow.afterUpdateSpisok(listAddresses);
    }

    private void prepareViewDetails() {
        setLayoutOrientation(JList.VERTICAL);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getSelectionModel().addListSelectionListener(e -> {
            selectedAddress = getSelectedValue();
            listWindow.setSelectAddress(selectedAddress);
        });
    }

//=========================================================

    public String getSelectedAddress() {
        return selectedAddress;
    }

    Set<String> getListAddresses() {
        return listAddresses;
    }

}
