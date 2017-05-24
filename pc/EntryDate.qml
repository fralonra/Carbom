import QtQuick 2.6
import QtQuick.Controls 1.4

Item {
    property string name
    property string text: picker.text

    width: 300
    height: 20
    Label {
        text: parent.name
        font.bold: true
        anchors.rightMargin: 20
    }
    Rectangle {
        x: 100
        height: 20
        width: 200
        color: "lightyellow"
        Label {
            id: picker
            anchors.fill: parent
            text: dateDialog.date
            MouseArea {
                anchors.fill: parent
                onClicked: dateDialog.open()
            }
        }
    }

    DialogDate {
        id: dateDialog
    }
}
