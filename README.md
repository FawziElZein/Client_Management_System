# Description

This project consist of a mobile application built with Back4App as mBaas (mobile Backend As A Service).The application can manage client purchases for the merchant (user of this application). All clients informations and their related purchases are stored on a hosted Parse Database.

Once Parse server is initialized on the mobile application, a local database will be initialized also. First, when the application is launched, this database is initialized by the data fetched from the online parse server. Then in case of network failure, every query that was meant to be executed on the online database will be executed on the local one. Once the connection is back, the SDK will take care of executing it online so no need to worry about internet failure.

# Architecture

![Project Architecture](https://github.com/FawziElZein/Client_Management_System/blob/master/architecture.png)

# Overview

### 1. Connect to Parse Server
<br>

In order to connect to parse server on back4app, you should start by getting your keys. After login and creating your back4App application go to ServerSettings -> Core Settings and retreive your AppId and ClientKey.

<div align="center">
<img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/parsesetup.jpg">
</div>

### 2. Purchase
<br>
Create and update purchase with related information (Client, Date of purchase, Category of the material, its weight, Payment method and some notes). Colors in the left of each purchase are setted when assigning color for the related category (See #4 Category). Swip left to delete it.
<br>
<br>
<table style="width:100%">
  <tr>
    <th>Create</th>
    <th>Update</th>
    <th>Delete</th>
  </tr>
  <tr>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/createpurchase.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/updatepurchase.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/deletepurchase.jpg"></td>
  </tr>
</table>

### 3. Client
<br>
Create and update Client information (name, phone number, address). Swip left to delete it.
<br>
<br>
<table style="width:100%">
  <tr>
    <th>Create</th>
    <th>Update</th>
    <th>Delete</th>
  </tr>
  <tr>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/createclient.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/updateclient.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/deleteclient.jpg"></td>
  </tr>
</table>

### 4. Category
<br>
Create and update Category (name, price, color). Swip left to delete it.
<br>
<br>
<table style="width:100%">
  <tr>
    <th>Create</th>
    <th>Delete</th>
    <th>SetColor</th>
  </tr>
  <tr>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/createcategory.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/colorcategory.jpg"></td>
    <td><img src="https://github.com/FawziElZein/Client_Management_System/blob/master/screenshots/deletecategory.jpg"></td>
  </tr>
</table>

