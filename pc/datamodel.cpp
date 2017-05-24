#include "datamodel.h"

DataModel::DataModel(QObject *parent)
    : QAbstractTableModel(parent)
{
}

DataModel::DataModel(const QList<Entry> &data)
{
    setSource(data);
}

DataModel::DataModel(const DataModel &other)
{
    m_source = other.m_source;
}

DataModel &DataModel::operator =(const DataModel &other)
{
    m_source = other.m_source;
    return *this;
}

QVariant DataModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    //if (role == Qt::DisplayRole && orientation == Qt::Vertical)
        //return section + 1;
    //else
        //return sourceModel()->headerData(section, orientation, role);
}

int DataModel::rowCount(const QModelIndex &parent) const
{
    //if (parent.isValid())
        //return 0;
    return m_source.size();
}

int DataModel::columnCount(const QModelIndex &parent) const
{
    //if (parent.isValid())
        //return 0;
    return Entry::IndexCount;
}

QVariant DataModel::data(const QModelIndex &index, int role) const
{
    if (!m_source.isEmpty()) {
        if (!index.isValid())// || role != Qt::DisplayRole)
            return QVariant();
        // Check boudaries
        if(index.column() < 0 || columnCount() <= index.column() ||
                index.row() < 0 || rowCount() <= index.row())
            return QVariant();
        if (role == Qt::DisplayRole)
            if (index.column() == 0)
                return index.row() + 1;
            else return m_source.at(index.row()).value(index.column());
        else {
        switch (role) {
            case EpcRole:
                return m_source.at(index.row()).value(Entry::Epc);
                break;
            case TypeRole:
                return m_source.at(index.row()).value(Entry::Type);
                break;
            case NameRole:
                return m_source.at(index.row()).value(Entry::Name);
                break;
            case StageRole:
                return m_source.at(index.row()).value(Entry::Stage);
                break;
            case StatusRole:
                return m_source.at(index.row()).value(Entry::Status);
                break;
            case TimeRole:
                return m_source.at(index.row()).value(Entry::Time);
                break;
            case LocationRole:
                return m_source.at(index.row()).value(Entry::Location);
                break;
            case KeeperRole:
                return m_source.at(index.row()).value(Entry::Keeper);
                break;
            case NoteRole:
                return m_source.at(index.row()).value(Entry::Note);
                break;
            case LoanDateRole:
                return m_source.at(index.row()).value(Entry::LoanDate);
                break;
            case ReturnDateRole:
                return m_source.at(index.row()).value(Entry::ReturnDate);
                break;
            case IndexRole:
                return index.row() + 1;
                break;
            default:
                break;
            }
        }
    }
    return QVariant();
}

QHash<int, QByteArray> DataModel::roleNames() const
{
    QHash<int, QByteArray> roles;
    roles[EpcRole] = "epc";
    roles[TypeRole] = "type";
    roles[NameRole] = "name";
    roles[StageRole] = "stage";
    roles[StatusRole] = "status";
    roles[TimeRole] = "time";
    roles[LocationRole] = "location";
    roles[KeeperRole] = "keeper";
    roles[NoteRole] = "note";
    roles[LoanDateRole] = "loanDate";
    roles[ReturnDateRole] = "returnDate";
    roles[IndexRole] = "index";
    return roles;
}

void DataModel::setSource(const QList<Entry> &arg)
{
    m_source = arg;
    emit sourceChanged();
    emit layoutChanged(); //important!!!!!!
}
