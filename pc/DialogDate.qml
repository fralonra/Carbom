import QtQuick 2.6
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.2

Dialog {
    property string date

    id: dateDialog
    modality: Qt.WindowModal
    title: qsTr("Choose a date")
    standardButtons: StandardButton.Ok | StandardButton.Cancel
    onAccepted: {
        if (clickedButton === StandardButton.Ok)
            date = Qt.formatDate(calendar.selectedDate, "yyyy-MM-dd")
    }

    Calendar {
        id: calendar
        width: parent ? parent.width : implicitWidth
        onDoubleClicked: dateDialog.click(StandardButton.Ok)
    }
}
