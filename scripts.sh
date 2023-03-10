sudo su
yum update -y
yum install httpd -y
service httpd start
chkconfig on
cd /var/www/html
mv home.html index.html