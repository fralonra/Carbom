import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.2
import QtQuick.Layouts 1.3

Dialog {
    property string entry

    width: 400
    height: 240
    modality: Qt.WindowModal
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    ColumnLayout {
        anchors.fill: parent
        spacing: 15
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
            name: qsTr("Return Date")
        }
        EntryText {
            id: note
            name: qsTr("Note")
        }
    }
    onAccepted: {
        if (keeper.text != "") {
            entry = entry + "KEEPER:" + keeper.text + "&"
            if (loanDate.text == "")
                entry = entry + "LOAN_DATE:" + Qt.formatDate(new Date(), "yyyy-MM-dd") + "&"
            else if (loanDate.text != "")
                entry = entry + "LOAN_DATE:" + loanDate.text + "&"
            if (returnDate.text != "")
                entry = entry + "RETURN_DATE:" + returnDate.text + "&"
            if (note.text != "")
                entry = entry + "NOTE:" + note.text + "&"
        }
    }
}
