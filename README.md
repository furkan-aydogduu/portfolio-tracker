# portfolio-tracker

# Project: Portfolio Tracking Service

### About:
 - The service provides an API to manage your cryptocurrency portfolio. 
 - The service uses BitFinex's public API to calculate currencies at the backend.
 - The service does not provide any authentication principle nor does not store any user information.
-  The service only accepts "POST" requests.
- The service only accepts "application/json" requests and does return only "application/json" responses.
- The service provides all cryptocurrency values in Euro currency.
- The service calculates the current market value of the cryptocurrency whenever it is requested
- The service provides profit / loss change of the cryptocurrency by comparing its value at the purchased time when it is requested

## Technologies used:
	  - Java 8 
	  - Maven 3 
	  - H2 Database
	  - Spring Boot 2.7.0-SNAPSHOT
	  - Spring Data JPA
  
 ##### Note: Client tests of each available method are included in the project. 
  ## API Details:
  ##### Service API URL : http://{host}/api/v1/portfolio/{action}/#{id}
    {host}    : Required for all requests
    {action}  : Required for all requests
    #{id}     : Required for actions: view, delete, update

### Possible actions / {action}: 
  #### [/list] : Method to list all the portfolio entries with currency price and market value at the current and purchased times.  
      Example request:
                       URL : http://localhost/api/v1/portfolio/list
                       Request Body  : No Body
                       Response Body : 
                                    [
                                      {
                                      "updatedTime": "2022-01-24T22:17:26.458",
                                      "currentCurrencyPrice": 31643,
                                      "amountPurchased": 15.2,
                                      "creationTime": "2022-01-24T22:17:26.458",
                                      "currentProfitLossRate": "0.9539%",
                                      "marketValueAtPurchasedTime": 476428.8,
                                      "id": 1,
                                      "marketValueAtCurrentTime": 480973.6,
                                      "walletLocation": "America",
                                      "cryptoCurrencyName": "btc"
                                   },
                                      {
                                      "updatedTime": "2022-01-24T22:17:26.469",
                                      "currentCurrencyPrice": 0.674154115,
                                      "amountPurchased": 2.3,
                                      "creationTime": "2022-01-24T22:17:26.469",
                                      "currentProfitLossRate": "-5.1666%",
                                      "marketValueAtPurchasedTime": 1.63503,
                                      "id": 2,
                                      "marketValueAtCurrentTime": 1.5505544644999998,
                                      "walletLocation": "Russia",
                                      "cryptoCurrencyName": "iot"
                                   },
                                ]
                                
  #### [/add] : Method to add a new portfolio entry.
      Example request:
                       URL : http://localhost/api/v1/portfolio/add
                       Request Body: 
                                      {
                                        "amountPurchased" : 0.52,
                                        "walletLocation" : "Talinn",
                                        "cryptoCurrencyName" : "btc"
                                      }
                       Response Body : 
                                    {
                                       "updatedTime": "2022-01-25T14:29:00.386",
                                       "currentCurrencyPrice": 32179,
                                       "amountPurchased": 0.52,
                                       "creationTime": "2022-01-25T14:29:00.386",
                                       "currentProfitLossRate": "0.0%",
                                       "marketValueAtPurchasedTime": 16733.08,
                                       "id": 11,
                                       "marketValueAtCurrentTime": 16733.08,
                                       "walletLocation": "Talinn",
                                       "cryptoCurrencyName": "btc"
                                    }
 
 #### [/delete] : Method to delete an existing portfolio entry.
      Example request:
                       URL : http://localhost/api/v1/portfolio/delete/1   -- 1 : id of the portfolio entry
                       Request Body  : No Body
                       Response Body : 
                                    {
                                        "message": "Record Deleted Successfully"
                                    }
                                    
 #### [/update] : Method to update an existing portfolio entry by purchased amount, wallet location or currency type or both.
      Example request:
                       URL : http://localhost/api/v1/portfolio/update/2  -- 2 : id of the portfolio entry
                       Request Body  :
                                      {
                                        "amountPurchased" : 0.40,
                                        "walletLocation" : "Talinn",
                                        "cryptoCurrencyName" : "btc"
                                      }
                       Response Body : 
                                     {
                                        "message": "Record Updated Successfully"
                                     }

 #### [/view] : Method to view the details of an existing portfolio entry. 
      Example request:
                       URL : http://localhost/api/v1/portfolio/view/2  -- 2 : id of the portfolio entry
                       Request Body  : No Body          
                       Response Body : 
                                     {
                                       "updatedTime": "2022-01-25T14:34:50.126",
                                       "currentCurrencyPrice": 32199.5,
                                       "amountPurchased": 0.4,
                                       "creationTime": "2022-01-25T14:28:29.639",
                                       "currentProfitLossRate": "-0.0326%",
                                       "marketValueAtPurchasedTime": 12884,
                                       "id": 2,
                                       "marketValueAtCurrentTime": 12879.800000000001,
                                       "walletLocation": "Talinn",
                                       "cryptoCurrencyName": "btc"
                                     }
                                    

 #### [/list-symbols] : Method to list all the symbols provided by the BitFinex API. Each symbol has a pattern like "[currency1][currency2]" or if the length of the symbol is longer than three then "[currency1]:[currency2]". The service handles the symbol parsing accordingly. So, when you want add a cryptocurrency to portfolio, you need to use [currency1] or [currency2] as crypto currency name.
 ##### e.g. "btcusd" -> btc/usd pair.
 ##### e.g. "btc:cnht" -> btc/cnht pair.
                 
      Example request:
                       URL : http://localhost/api/v1/portfolio/list-symbols
                       Request Body  : No Body          
                       Response Body : 
                                     [
                                       "btcusd",
                                       "ltcusd",
                                       "ltcbtc",
                                       "ethusd",
                                       "ethbtc",
                                       "etcbtc",
                                       "etcusd",
                                       "rrtusd",
                                       "zecusd",
                                       "zecbtc",
                                       "xmrusd",
                                       "xmrbtc",
                                       "dshusd",
                                       "dshbtc",
                                       "btceur",
                                       "btcjpy",
                                       .
                                       .
                                       .
                                    ]    
                                    
                                    
#### [/view-portfolio-market-value] : Method to get the combined value of the portfolio in Euro currency. 
      Example request:
                       URL : http://localhost/api/v1/portfolio/view-portfolio-market-value
                       Request Body  : No Body          
                       Response Body : 
                                     {
                                        "message": "4984785.490333537"
                                     }
