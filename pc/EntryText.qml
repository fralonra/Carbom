import QtQuick 2.6
import QtQuick.Controls 1.4

Item {
    property string name
    property string text: input.text

    width: 300
    height: 20
    Label {
        text: parent.name
        font.bold: true
        anchors.rightMargin: 20
    }
    TextField {
        id: input
        x: 100
        width: 200
    }
}
