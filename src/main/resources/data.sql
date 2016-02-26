insert into users(username,	password, enabled) values ('admin', 'admin', true);

insert into authorities (username, authority) values ('admin', 'admin');

insert into UserProfile (userId, email, firstName, lastName, name, username, imageUrl) values ('admin', 'samer.abdelkafi@mailserver.com', 'Samer', 'ABDELKAFI', 'name', 'user', 'resources/dist/img/photo.png');

	