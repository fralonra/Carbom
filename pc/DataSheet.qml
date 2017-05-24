import QtQuick 2.6

Item {
    property string key
    property string value

    Text {
        id: key
        text: parent.key + ":     "
        font.bold: true
        font.pointSize: 15
        anchors.margins: 5
        anchors.rightMargin: 20
    }
    Text {
        y: 3
        text: parent.value
        font.pointSize: 12
        anchors.left: key.right
    }
}
