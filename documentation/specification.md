# Specification

## Planned Features

**Auth**
- Register via (google?)
- Logging in
- Logging out

**Management**
- Add, edit delete a print/product
  - price
  - stock
  - ownership 
- Add, edit delete bundles/discounts
  - price
  - prints/products included
  - automatically determines price split by ownership
    - can be overridden
- Input starting cash on hand

**Point of Sale**
- Select prints/products and bundles
  - can edit prices
- Checkout
  - Card
    - routes to square app with price calculated
  - Cash
    - calculates change
    - updates cash on hand accordingly
  - records transaction info
    - price
      - price breakdown
    - timestamp
    - cash/card
    - items sold
  - decrements stock accordingly

**Review**
- List all transactions
  - transaction breakdown
- Profit
  - breakdown by artist
- Remaining stock
- Remaining cash on hand
- Sales analytics

## Frontend
- Jetpack Compose
- Async Images for loading images from url
- dotenv for handling environment variables

### To Do:
- [x] Home Page
- [ ] custom theme
- Auth
  - [ ] everything
- Management
  - [x] page
  - print
     - [x] add
     - [ ] edit
     - [ ] delete
  - bundle
    - [x] add
    - [ ] edit
    - [ ] delete
  - [ ] search and filter
- Point of Sale
  - [x] page
  - print
    - [x] add
    - [ ] edit
    - [x] delete
  - bundle
    - [x] add
    - [ ] edit
    - [x] delete
  - [ ] search and filter
  - [x] cart
  - [ ] handle card payment via square 
- Review
  - [x] list of sales
  - [ ] profit breakdown by artist
  - [ ] analytics

## Backend

 - ktor for requests
 - cloudinary for image hosting

### Endpoints

|     url     |   method   |   input    |       output       | description      |
|:-----------:|:----------:|:----------:|:------------------:|:-----------------|
| ``/print``  |  ``GET``   |            |  ``list: prints``  | gets all prints  |
| ``/print``  |  ``POST``  | ``print``  | ``bool: success``  | creates a print  | 
| ``/print``  | ``DELETE`` |  ``name``  | ``bool: success``  | deletes a print  |
| ``/bundle`` |  ``GET``   |            | ``list: bundles``  | gets all bundles |
| ``/bundle`` |  ``POST``  | ``bundle`` | ``bool: success``  | creates a bundle |
| ``/bundle`` | ``DELETE`` |  ``name``  | ``bool: success``  | deletes a bundle |




### Types
