create table users(
	username varchar_ignorecase(64) not null primary key,
	password varchar_ignorecase(64) not null,
	enabled boolean not null
);

create table authorities (
	username varchar_ignorecase(64) not null,
	authority varchar_ignorecase(64) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

create table persistent_logins (
  username varchar(64) not null,
	series varchar(64) primary key,
	token varchar(64) not null,
	last_used timestamp not null);

create table UserConnection (
  userId varchar(64) not null,
	providerId varchar(64) not null,
	providerUserId varchar(255),
	rank int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

create table UserProfile (
  userId varchar(64) not null,
  email varchar(255),
  firstName varchar(64),
  lastName varchar(64),
  name  varchar(64),
  username varchar(64),
  imageUrl varchar(512),
  primary key (userId));
create unique index UserProfilePK on UserProfile(userId);