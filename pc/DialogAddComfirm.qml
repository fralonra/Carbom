import QtQuick 2.6
import QtQuick.Dialogs 1.2

Item {
    function comfirmAdd(epc) {
        addComfirm.epc = epc;
        addComfirm.open();
        console.log(epc)
        if (addComfirm.accepted())
            return 1;
        else
            return 0;
        //return addComfirm.returnValue;
    }

    MessageDialog {
        property string epc
        property int returnValue: 0

        id: addComfirm
        objectName: "comfirm"
        title: qsTr("Please Comfirm")
        text: qsTr("Duplicated entry: " + epc + ".\nAre you sure to override this entry?")
        standardButtons: StandardButton.Ok | StandardButton.Cancel

        onAccepted: {
            returnValue = 1;
        }
    }
}
