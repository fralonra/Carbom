import QtQuick 2.6
import QtQuick.Controls 2.0

Item {
    signal modified()

    property string entry

    property string epc
    property string type
    property string name
    property string stage
    property string status
    property string time
    property string location
    property string keeper
    property string note
    property string loanDate
    property string returnDate

    function resetSheet() {
        typeSheet.value = Qt.binding(function() {
            return type
        })
        nameSheet.value = Qt.binding(function() {
            return name
        })
        stageSheet.value = Qt.binding(function() {
            return stage
        })
        statusSheet.value = Qt.binding(function() {
            return status
        })
        locationSheet.value = Qt.binding(function() {
            return location
        })
        noteSheet.value = Qt.binding(function() {
            return note
        })
    }

    width: 100
    DataSheet {
        id: epcSheet
        key: qsTr("EPC")
        value: parent.epc
        editable: false
        x: 10
        y: 10
    }
    Rectangle {
        x: 5
        y: 60
        width: 300
        height: 1
        color: "grey"
    }
    DataSheet {
        id: typeSheet
        key: qsTr("Type")
        value: parent.type
        editable: true
        x: 10
        y: 90
    }
    DataSheet {
        id: nameSheet
        key: qsTr("Name")
        value: parent.name
        editable: true
        x: 10
        y: 140
    }
    DataSheet {
        id: stageSheet
        key: qsTr("Stage")
        value: parent.stage
        editable: true
        x: 10
        y: 190
    }
    DataSheet {
        id: statusSheet
        key: qsTr("Status")
        value: parent.status
        editable: true
        x: 10
        y: 240
    }
    DataSheet {
        id: timeSheet
        key: qsTr("Time")
        value: parent.time
        editable: false
        x: 10
        y: 290
    }
    DataSheet {
        id: locationSheet
        key: qsTr("Location")
        value: parent.location
        editable: true
        x: 10
        y: 340
    }
    DataSheet {
        id: keeperSheet
        key: qsTr("Keeper")
        value: parent.keeper
        editable: false
        x: 10
        y: 390
    }
    Rectangle {
        x: 5
        y: 440
        width: 300
        height: 1
        color: "grey"
    }
    DataSheet {
        id: loanDateSheet
        key: qsTr("Loan Date")
        value: parent.loanDate
        editable: false
        x: 10
        y: 470
    }
    DataSheet {
        id: returnDateSheet
        key: qsTr("Return Date")
        value: parent.returnDate
        editable: false
        x: 10
        y: 520
    }
    DataSheet {
        id: noteSheet
        key: qsTr("Note")
        value: parent.note
        editable: true
        x: 10
        y: 570
    }
    Row {
        x: 10
        y: 620
        spacing: 20
        Button {
            id: modify
            text: qsTr("Modify")
            onClicked: {
                entry = ""
                if (typeSheet.hasModified)
                    entry = entry + "TYPE:" + typeSheet.value + "&"
                if (nameSheet.hasModified)
                    entry = entry + "NAME:" + nameSheet.value + "&"
                if (stageSheet.hasModified)
                    entry = entry + "STAGE:" + stageSheet.value + "&"
                if (statusSheet.hasModified)
                    entry = entry + "STATUS:" + statusSheet.value + "&"
                if (locationSheet.hasModified)
                    entry = entry + "LOCATION:" + locationSheet.value + "&"
                if (noteSheet.hasModified)
                    entry = entry + "NOTE:" + noteSheet.value + "&"
                if (entry != "")
                    modified()
            }
        }
        Button {
            id: reset
            text: qsTr("Reset")
            onClicked: resetSheet()
        }
    }
    onEpcChanged: resetSheet()
}
