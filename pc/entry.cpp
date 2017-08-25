#include "entry.h"

const QString Entry::entryDiv = "&";
const QString Entry::dataDiv = "\n";
const QString Entry::storedStr = "在库";

const QMap<Entry::Index, QString> Entry::indexText =
{{Entry::Epc, "EPC:"},
 {Entry::Type, "TYPE:"},
 {Entry::Name, "NAME:"},
 {Entry::Stage, "STAGE:"},
 {Entry::Status, "STATUS:"},
 {Entry::Time, "TIME:"},
 {Entry::Location, "LOCATION:"},
 {Entry::Keeper, "KEEPER:"},
 {Entry::Note, "NOTE:"},
 {Entry::LoanDate, "LOAN_DATE:"},
 {Entry::ReturnDate, "RETURN_DATE:"}};

Entry::Entry(QObject *parent) : QObject(parent)
{
}

Entry::Entry(const QString &data, QObject *parent) : QObject(parent)
{
    init(data);
}

Entry::Entry(const Entry &other)
{
    m_data = other.m_data;
}

Entry &Entry::operator =(const Entry &other)
{
    m_data = other.m_data;
}

bool Entry::operator ==(const Entry &other) const
{
    return (m_data == other.m_data);
}

void Entry::init(const QString &data)
{
    m_data.insert(Epc, phraseData(indexText.value(Epc), data));
    m_data.insert(Type, phraseData(indexText.value(Type), data));
    m_data.insert(Name, phraseData(indexText.value(Name), data));
    m_data.insert(Stage, phraseData(indexText.value(Stage), data));
    m_data.insert(Status, phraseData(indexText.value(Status), data));
    m_data.insert(Time, phraseData(indexText.value(Time), data));
    m_data.insert(Location, phraseData(indexText.value(Location), data));
    m_data.insert(Keeper, phraseData(indexText.value(Keeper), data));
    m_data.insert(Note, phraseData(indexText.value(Note), data));
    m_data.insert(LoanDate, phraseData(indexText.value(LoanDate), data));
    m_data.insert(ReturnDate, phraseData(indexText.value(ReturnDate), data));
}

void Entry::modify(const QString &data)
{
    QStringList text = data.split("&");
    text.removeLast();
    for (QString temp : text) {
        QString key = temp.split(":").at(0) + ":";
        QString value = temp.split(":").at(1);
        m_data.insert(indexText.key(key), value);
    }
}


void Entry::insert(const Index index, const QString &value)
{
    m_data.insert(index, value);
}

const QString Entry::value(const Index index) const
{
    return m_data.value(index);
}

const QString Entry::value(const int index) const
{
    return m_data.value(static_cast<Index>(index));
}

const QString Entry::value(const QString &index) const
{
    return m_data.value(indexText.key(index));
}

const QString Entry::toString()
{
    QString string;
    QMapIterator<Index, QString> i(m_data);
    while (i.hasNext()) {
        i.next();
        string.append(indexText.value(i.key()) + i.value() + entryDiv);
    }
    return string.append(dataDiv);
}

bool Entry::isStored() const
{
    return (m_data.value(Keeper) == storedStr);
}

int Entry::sn() const
{
    return m_data.value(Epc).split(QRegularExpression(".*-0*"))[1].toInt();
}

void Entry::setEpc(const QString &arg) {
    m_data.insert(Epc, arg);
    emit epcChanged();
}

void Entry::setType(const QString &arg) {
    m_data.insert(Type, arg);
    emit typeChanged();
}

void Entry::setName(const QString &arg) {
    m_data.insert(Name, arg);
    emit nameChanged();
}

void Entry::setStage(const QString &arg) {
    m_data.insert(Stage, arg);
    emit stageChanged();
}

void Entry::setStatus(const QString &arg) {
    m_data.insert(Status, arg);
    emit statusChanged();
}

void Entry::setTime(const QString &arg) {
    m_data.insert(Time, arg);
    emit timeChanged();
}

void Entry::setLocation(const QString &arg) {
    m_data.insert(Location, arg);
    emit locationChanged();
}

void Entry::setKeeper(const QString &arg) {
    m_data.insert(Keeper, arg);
    emit keeperChanged();
}

void Entry::setNote(const QString &arg) {
    m_data.insert(Note, arg);
    emit noteChanged();
}

void Entry::setLoanDate(const QString &arg) {
    m_data.insert(LoanDate, arg);
    emit loanDateChanged();
}

void Entry::setReturnDate(const QString &arg) {
    m_data.insert(ReturnDate, arg);
    emit returnDateChanged();
}

const QString Entry::phraseData(const QString &key, const QString &data)
{
    if (data.contains(key))
        return data.split(key).at(1).split("&").at(0);
    return "";
}

const QString Entry::phraseData(const Index &index, const QString &data)
{
    QString key = indexText.value(index);
    if (data.contains(key))
        return data.split(key).at(1).split("&").at(0);
    return "";
}
