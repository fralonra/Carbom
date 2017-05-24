import QtQuick 2.6
import QtQuick.Controls 1.4

Item {
    property string key
    property string value : input.text

    Text {
        id: key
        text: parent.key + ":     "
        font.bold: true
        font.pointSize: 15
        anchors.margins: 5
        anchors.rightMargin: 20
    }
    TextField {
        id: input
        y: 3
        font.pointSize: 12
        anchors.left: key.right
    }
}
