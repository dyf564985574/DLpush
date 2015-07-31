CREATE TABLE user_link(
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT,
  uid TEXT,
  linkphone TEXT,
  linkname TEXT
) ;
CREATE TABLE notices(
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  id  TEXT,
  title TEXT,
  content TEXT,
  stoptime TEXT,
  uid TEXT,
  info TEXT,
  sendtime TEXT,
  readnum TEXT,
  countnum TEXT,
  notifystatus TEXT,
  notifytype TEXT,
  nid TEXT,
  isread TEXT
) ;
CREATE TABLE news(
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT,
  title TEXT,
  content TEXT,
  info TEXT,
  creatdate TEXT,
  uid TEXT,
  newsstatus TEXT
);
CREATE TABLE suggest(
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT,
  uid TEXT,
  content TEXT,
  creatdate TEXT,
  type TEXT,
  companyname TEXT,
  status TEXT
);
CREATE TABLE propose(
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT,
  uid TEXT,
  content TEXT,
  creatdate TEXT,
  type TEXT,
  companyname TEXT,
  status TEXT
) ;
CREATE TABLE user(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    id TEXT,
    ammeterid TEXT,
    linkman TEXT,
    passwd TEXT,
    roleid TEXT,
    companyname TEXT,
    companyaddress TEXT,
    phone TEXT,
    isapp TEXT,
    line TEXT,
    companytype TEXT,
    companyclassify TEXT,
    managerid TEXT
  ) ;
  CREATE TABLE stopnotices(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    day TEXT,
    num TEXT
  ) ;
  CREATE TABLE tempnotices(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    day TEXT,
    num TEXT
  ) ;
  CREATE TABLE changenotices(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    day TEXT,
    num TEXT
  );
   CREATE TABLE newnum(
      _id INTEGER PRIMARY KEY AUTOINCREMENT,
      day TEXT,
      num TEXT
    );




