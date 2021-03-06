#include "data.h"

Data::Data(QObject *parent) : QObject(parent)
{
    connect(&watcher, SIGNAL(fileChanged(const QString&)), SLOT(setFile(const QString&)));
}

void Data::setFile(const QString &arg) {
    if (arg.startsWith("file://"))
        m_file = QQmlFile::urlToLocalFileOrQrc(arg);
    else m_file = arg;
    if (QFile::exists(m_file)) {
        QFile file(m_file);
        if (file.open(QIODevice::ReadOnly)) {
            QString data(file.readAll());
            setTitle(QFileInfo(file).fileName());

            m_allData.clear();
            m_allEpc.clear();
            m_allTable.clear();

            modified = false;
            m_allData = data.split("\n");
            m_allData.removeLast();
            if (!m_table.isEmpty())
                    m_table.clear();
            for (QString line : m_allData) {
                Entry entry(line);
                m_allEpc.append(entry.value(Entry::Epc));
                m_table.append(entry);
            }
            m_allTable = m_table;
            setTable(m_table);
            setData(m_allData);
            //setList(m_allEpc);
            //setCount(m_allEpc.length());

            watcher.addPath(m_file);
        }
    } else {
        clear();
    }
    emit fileChanged();
}

void Data::setTitle(const QString &arg) {
    m_title = arg;
    if (!arg.isEmpty())
        m_title = m_title + " - ";
    emit titleChanged();
}

void Data::setList(const QStringList &arg) {
    m_list = arg;
    emit listChanged();
}

void Data::setReturnList(const QList<Entry> &arg) {
    m_returnList = arg;
    emit returnListChanged();
}

void Data::setTable(const QList<Entry> &arg) {
    m_table = arg;
    if (modified) {
        if (allShown)
            m_allTable = m_table;
        else { // not all data showed
            for (Entry entry : m_table) {
                QString epc = entry.epc();
                for (Entry e : m_allTable) {
                    int i = m_allTable.indexOf(e);
                    if (e.epc() == epc) {
                        e = entry;
                        m_allTable.replace(i, e);
                        break;
                    } else if (e == m_allTable.last()) // add entry
                        m_allTable.append(entry);
                }
            }
        }
        modified = false;
    }
    emit tableChanged();
    m_list.clear();
    for (Entry entry : m_table)
        m_list.append(entry.epc());
    setList(m_list);
    m_returnList.clear();
    for (Entry entry : m_allTable) {
        if (!entry.isStored())
            m_returnList.append(entry);
    }
    setReturnList(m_returnList);
}

void Data::setCount(const int &arg) {
    //m_count = arg;
    emit countChanged();
}

void Data::setPosition(const int &arg) {
    m_position = arg;
    if (m_data.isEmpty()) {
        m_position = -1;
    } else {
        Entry entry = m_table.at(m_position);
        setEpc(entry.value(Entry::Epc));
        setType(entry.value(Entry::Type));
        setName(entry.value(Entry::Name));
        setStage(entry.value(Entry::Stage));
        setStatus(entry.value(Entry::Status));
        setTime(entry.value(Entry::Time));
        setLocation(entry.value(Entry::Location));
        setKeeper(entry.value(Entry::Keeper));
        setNote(entry.value(Entry::Note));
        setLoanDate(entry.value(Entry::LoanDate));
        setReturnDate(entry.value(Entry::ReturnDate));
    }
    emit positionChanged();
}

void Data::setEpc(const QString &arg) {
    m_epc = arg;
    emit epcChanged();
}

void Data::setType(const QString &arg) {
    m_type = arg;
    emit typeChanged();
}

void Data::setName(const QString &arg) {
    m_name = arg;
    emit nameChanged();
}

void Data::setStage(const QString &arg) {
    m_stage = arg;
    emit stageChanged();
}

void Data::setStatus(const QString &arg) {
    m_status = arg;
    emit statusChanged();
}

void Data::setTime(const QString &arg) {
    m_time = arg;
    emit timeChanged();
}

void Data::setLocation(const QString &arg) {
    m_location = arg;
    emit locationChanged();
}

void Data::setKeeper(const QString &arg) {
    m_keeper = arg;
    emit keeperChanged();
}

void Data::setNote(const QString &arg) {
    m_note = arg;
    emit noteChanged();
}

void Data::setLoanDate(const QString &arg) {
    m_loanDate = arg;
    emit loanDateChanged();
}

void Data::setReturnDate(const QString &arg) {
    m_returnDate = arg;
    emit returnDateChanged();
}

void Data::open(const QString &file)
{
    setFile(file);
}

void Data::importXls(const QString &file)
{
    QXlsx::Document xlsx(QQmlFile::urlToLocalFileOrQrc(file));
    int maxRow = 1;
    for (int row = 2; ; ++row) {
        if (xlsx.read(row, 1).isNull()) {
            maxRow = row - 1;
            break;
        }
    }
    QStringList data;
    for (int row = 2; row <= maxRow; ++row) {
        QString entry;
        for (int col = 1; col <= Entry::IndexCount; ++col) {
            QVariant cell = xlsx.read(row, col);
            if (!cell.isNull())
                entry.append(Entry::indexText.value(static_cast<Entry::Index>(col - 1))
                        + cell.toString() + Entry::entryDiv);
        }
        entry.append(Entry::dataDiv);
        data.append(entry);
    }
    add(data);
    save();// avoid can't reset after importing without saving
}

void Data::exportXls(const QString &file)
{
    QXlsx::Document xlsx(QQmlFile::urlToLocalFileOrQrc(file));
    for (int col = 1; col <= Entry::xlsxHeader.length(); ++col) {
        xlsx.write(1, col, Entry::xlsxHeader.at(col - 1));
    }
    for (Entry e : m_allTable) {
        int row = m_allTable.indexOf(e);
        for (int col = 1; col <= Entry::IndexCount; ++col) {
            xlsx.write(row + 2, col, e.value(static_cast<Entry::Index>(col - 1)));
        }
    }
    xlsx.save();
}

void Data::save()
{
    saveData(m_file);
}

void Data::saveAs(const QString &file)
{

}

void Data::close() {
    setFile("");
}

void Data::prev() {
    if (m_position > 0) {
        setPosition(--m_position);
    }
}

void Data::next() {
    if (m_position < m_count - 1) {
        setPosition(++m_position);
    }
}

void Data::first() {
    if (m_position != 0) {
        setPosition(0);
    }
}

void Data::last() {
    if (m_position != m_table.size() - 1) {
        setPosition(m_table.size() - 1);
    }
}

void Data::find(const QString &arg) {
    if (!arg.isEmpty()) {
        if (allShown) {
            QString temp = arg;
            if (temp.at(0) == "")
                temp = temp.remove(0, 1);
            QStringList m_querylist = temp.split("&");
            m_querylist.removeLast();
            m_result.clear();
            for (QString entry : m_querylist) {
                QString key = entry.split(":").at(0) + ":";
                QString value = entry.split(":").at(1);
                for (Entry data : m_table) {
                    if (data.value(key).contains(value)) {
                        if (!m_result.contains(data))
                            m_result.append(data);
                    } else if (m_result.contains(data)) {
                        m_result.removeOne(data);
                    }
                }
            }
            if (!m_result.isEmpty()
                    && m_result.size() != m_allTable.size()) {
                setTable(m_result);
                allShown = false;
            }
        } else {
            reset();
            find(arg);
        }
    }
}

void Data::reset() {
    if (!m_result.isEmpty()) {
        m_result.clear();
        setTable(m_allTable);
        //setData(m_allData);
        setPosition(0);
        allShown = true;
    }
}

int Data::add(const QString &arg, int mode)
{
    Entry entry(arg);
    for (Entry e : m_table) {
        if (e == entry)
            break;
        else if (e.epc() == entry.epc()) {
            if (mode == 1) { // All Ok
                m_table.replace(m_table.indexOf(e), entry);
                break;
            }
            if (mode == 2) { // All Cancel
                break;
            }
            QMessageBox box;
            box.setStandardButtons(QMessageBox::YesToAll | QMessageBox::Ok | QMessageBox::Cancel | QMessageBox::NoToAll);
            box.setText(tr("Duplicated entry: ") + e.epc() + "\n" + tr("Are you sure to override this entry?"));
            int ret = box.exec();
            if (ret == QMessageBox::YesToAll) {
                m_table.replace(m_table.indexOf(e), entry);
                mode = 1;
            }
            else if (ret == QMessageBox::Ok ) {
                m_table.replace(m_table.indexOf(e), entry);
            }
            else if (ret == QMessageBox::NoToAll) {
                mode = 2;
            }
            break;
        } else if (m_table.indexOf(e) == (m_table.size() - 1)) {
            m_table.append(entry);
            break;
        }
    }
    return mode;
}

void Data::add(const QStringList &arg)
{
    int mode = 0;
    for (QString entry : arg) {
        mode = add(entry, mode);
    }
    modified = true;
    setTable(m_table);
}

void Data::remove(const int index, bool multi)
{
    for (Entry entry : m_table) {
        if (m_table.indexOf(entry) == index) {
            m_table.removeOne(entry);
            if (!allShown) {
                QString epc = entry.epc();
                for (Entry e : m_allTable) {
                    if (e.epc() == epc) {
                        m_allTable.removeOne(e);
                        break;
                    }
                }
            }
            break;
        }
    }
    if (!multi) {
        modified = true;
        setTable(m_table);
    }
}

void Data::remove(const QString &epc, bool multi)
{
    for (Entry entry : m_table) {
        if (entry.epc() == epc) {
            m_table.removeOne(entry);
            if (!allShown)
                m_allTable.removeOne(entry);
            break;
        }
    }
    if (!multi) {
        modified = true;
        setTable(m_table);
    }
}

void Data::remove(const QList<int> &list)
{
    QStringList epcs;
    for (int i : list) {
        epcs.append(m_table.at(i).epc());
    }
    for (QString epc : epcs)
        remove(epc, true);
    modified = true;
    setTable(m_table);
}

void Data::clear()
{
    m_allData.clear();
    m_allEpc.clear();
    m_allTable.clear();
    m_table.clear();
    setTitle("");
    setData(m_allData);
    setList(m_allEpc);
    setTable(m_allTable);
    //setCount(0);
    setPosition(-1);
    setEpc("");
    setType("");
    setName("");
    setStage("");
    setStatus("");
    setTime("");
    setLocation("");
    setKeeper("");
    setNote("");
    setLoanDate("");
    setReturnDate("");
}

void Data::sort()
{
    if (m_result.isEmpty()) {
        QList<int> sn_list;
        QList<Entry> sorted_table;
        for (Entry entry : m_table) {
            sn_list.append(entry.sn());
        }
        qSort(sn_list);
        for (int sn : sn_list) {
            for (Entry entry : m_table) {
                if (entry.sn() == sn) {
                    sorted_table.append(entry);
                    break;
                }
            }
        }
        modified = true;
        setTable(sorted_table);
    }
}

void Data::modify(const QString &epc, const QString &data)
{
    for (Entry entry : m_table) {
        if (entry.epc() == epc) {
            int i = m_table.indexOf(entry);
            entry.modify(data);
            m_table.replace(i, entry);
            break;
        }
    }
    modified = true;
    setTable(m_table);
}

void Data::loan(const QList<int> &list, const QString &data)
{
    for (int i : list) {
        if (i >= m_table.size())
            break;
        Entry entry = m_table.at(i);
        entry.modify(data);
        m_table.replace(i, entry);
    }
    modified = true;
    setTable(m_table);
}

void Data::returnBack(const QList<int> &list)
{
    for (int index : list) {
        if (index >= m_returnList.size())
            break;
        QString epc = m_returnList.at(index).epc();
        for (int i = 0; i < m_table.size(); ++i) {
            Entry entry = m_table.at(i);
            if (entry.epc() == epc) {
                entry.insert(Entry::Keeper, Entry::storedStr);
                entry.insert(Entry::LoanDate, "");
                m_table.replace(i, entry);
                break;
            }
        }
    }
    modified = true;
    setTable(m_table);
}

void Data::setData(const QStringList &arg)
{
    m_data = arg;
    setPosition(0);
}

void Data::saveData(const QString &fileName)
{
    QFile file(fileName);
    if (file.exists()) {
        if (file.open(QIODevice::WriteOnly | QIODevice::Text)) {
            QTextStream out(&file);
            out.setCodec("UTF-8");
            out << toString();
        }
    }
    file.close();
}

const QString Data::toString()
{
    QString text;
    for (Entry entry : m_allTable)
        text.append(entry.toString());
    return text;
}
