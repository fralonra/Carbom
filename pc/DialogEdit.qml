import QtQuick 2.6
import QtQuick.Dialogs 1.2

Dialog {
    property string entry
    property string mode: ""

    width: 340
    height: 440
    modality: Qt.WindowModal
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    Column {
        spacing: 15
        EntryText {
            id: epc
            name: qsTr("EPC")
        }
        EntryText {
            id: type
            name: qsTr("Type")
        }
        EntryText {
            id: name
            name: qsTr("Name")
        }
        EntryText {
            id: stage
            name: qsTr("Stage")
        }
        EntryText {
            id: status
            name: qsTr("Status")
        }
        EntryDate {
            id: time
            name: qsTr("Time")
        }
        EntryText {
            id: location
            name: qsTr("Location")
        }
        EntryText {
            id: keeper
            name: qsTr("Keeper")
        }
        EntryDate {
            id: loanDate
            name: qsTr("Loan Date")
        }
        EntryDate {
            id: returnDate
            name: qsTr("Reurn Date")
        }
        EntryText {
            id: note
            name: qsTr("Note")
        }
    }
    onAccepted: {
        if (epc.text != "")
            entry = entry + "EPC:" + epc.text + "&"
        if (type.text != "")
            entry = entry + "TYPE:" + type.text + "&"
        if (name.text != "")
            entry = entry + "NAME:" + name.text + "&"
        if (stage.text != "")
            entry = entry + "STAGE:" + stage.text + "&"
        if (status.text != "")
            entry = entry + "STATUS:" + status.text + "&"
        if (time.text != "")
            entry = entry + "TIME:" + time.text + "&"
        if (location.text != "")
            entry = entry + "LOCATION:" + location.text + "&"
        if (keeper.text != "")
            entry = entry + "KEEPER:" + keeper.text + "&"
        if (loanDate.text != "")
            entry = entry + "LOAN_DATE:" + loanDate.text + "&"
        if (returnDate.text != "")
            entry = entry + "RETURN_DATE:" + returnDate.text + "&"
        if (note.text != "")
            entry = entry + "NOTE:" + note.text + "&"
        if (mode === "inbound") {
            if (time.text == "")
                entry = entry + "TIME:" + Qt.formatDate(new Date(), "yyyy-MM-dd") + "&"
            if (keeper.text == "")
                entry = entry + "KEEPER:在库&"
        }
    }
}
