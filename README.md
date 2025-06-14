# 💰 Money Saving Application - Dynamic Financial Management Application

A modern, feature-rich financial management application designed to help users save money, manage expenses, and make smarter financial decisions through innovative features like Reverse Spend challenges, Guilt Save rules, Emergency Shield protection, and more.


## 🌟 Features

### 📊 Dashboard
The central hub displays a comprehensive overview of your financial health:
- **Financial Summary**: View your total balance, income, expenses, and savings at a glance
- **Weekly Reports**: AI-generated summaries of your financial activity and accomplishments
- **Financial Stories**: Interactive cards highlighting milestones, alerts, and personalized tips
- **Recent Transactions**: Quick access to your latest financial activities

### 🔄 Reverse Spend
An innovative approach to budgeting that rewards you for spending less:
- **Create Category Challenges**: Set budget limits for specific spending categories
- **Earn Rewards**: Get cashback (3-5%) on the unused portion of your budget
- **Real-time Tracking**: Monitor your spending against budget limits
- **Challenge History**: View your past challenges and success rate

### 💸 Guilt Save
Automatically save money when you overspend in selected categories:
- **Create Custom Rules**: Define percentage-based saving rules for specific categories
- **Automated Transfers**: Money is automatically moved to savings when triggered
- **Flexible Destinations**: Choose where your guilt savings go (emergency fund, investments, etc.)
- **Impact Visualization**: See how much your guilt has contributed to your savings

### 🛡️ Emergency Shield
Protect your emergency funds with trusted friend approval system:
- **Designate Guardians**: Choose trusted friends to approve emergency withdrawals
- **Threshold Settings**: Set amount limits that require approval
- **Request Management**: Submit and track withdrawal requests
- **Secure Notifications**: Guardians receive secure alerts when approval is needed

### 👥 Group Investment
Pool resources with friends and family for collective investment opportunities:
- **Create Investment Pots**: Set up shared goals with flexible contribution structures
- **Member Management**: Invite participants and track individual contributions
- **Goal Tracking**: Monitor progress toward collective investment targets
- **Return Projection**: View estimated returns based on investment type

### 📱 Subscription Trimmer+
Optimize your subscription expenses and discover sharing opportunities:
- **Subscription Dashboard**: View all recurring expenses in one place
- **Usage Analysis**: Track how often you use each service
- **Sharing Recommendations**: Discover compatible friends for family plans
- **Savings Calculator**: See potential savings from subscription optimization

## 🛠️ Technology Stack

### Backend
- **Java 17**: Core programming language
- **Spring Boot 3.2.3**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access and ORM
- **MySQL 8.0**: Relational database

### Frontend
- **HTML5/CSS3**: Structure and styling
- **JavaScript (ES6+)**: Client-side logic
- **Fetch API**: Asynchronous HTTP requests
- **Chart.js**: Data visualization

### Security
- **JWT**: Secure authentication tokens
- **BCrypt**: Password hashing
- **HTTPS**: Secure communication
- **CORS**: Cross-origin resource sharing protection

## 📁 Database Schema

The application uses a relational MySQL database with the following key tables:

- **users**: Core user information and account balances
- **accounts**: Bank, savings, and investment accounts
- **transactions**: All financial activities with categories
- **reverse_spend**: Budget challenges and rewards
- **guilt_save**: Automatic saving rules
- **emergency_shield**: Protection settings and guardian info
- **group_investment_pots**: Collective investment opportunities
- **subscriptions**: Recurring expenses and sharing options

## 🚀 Installation and Setup

### Prerequisites
- Java JDK 17 or higher
- Maven 3.8+
- MySQL 8.0

### Database Setup
```sql
CREATE DATABASE moneysaving;
USE moneysaving;
```

### Configuration
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moneysaving?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Build and Run
```bash
# Clone the repository
git clone https://github.com/yourusername/moneysaving.git
cd moneysaving

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 📚 API Documentation

### User Endpoints
- `GET /users/{id}`: Retrieve user profile
- `GET /transactions/user/{id}`: Get user transactions
- `GET /transactions/user/{id}/category-wise`: Get spending by category

### Feature Endpoints
- `GET /reverse-spend/user/{id}/active`: Get active spending challenges
- `GET /guilt-save/user/{id}/active`: Get active saving rules
- `GET /emergency-shield/user/{id}/active`: Get active protection settings
- `GET /group-investment/user/{id}`: Get investment pots
- `GET /subscriptions/user/{id}`: Get subscriptions

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) for the framework
- [MySQL](https://www.mysql.com/) for the database
- [Chart.js](https://www.chartjs.org/) for data visualization

## Images
![image](https://github.com/user-attachments/assets/38bcac4c-4043-4eb6-aa1f-63c272fa1395)
![image](https://github.com/user-attachments/assets/8eaedeb6-ef3b-410f-b343-288deb5b5ad9)
![image](https://github.com/user-attachments/assets/b222117f-c439-40b8-8701-1509df9eb017)
![image](https://github.com/user-attachments/assets/4d646945-6f6b-4ac0-bc1f-e6b46c3e1bca)
![image](https://github.com/user-attachments/assets/182016f0-0db6-4042-a775-c71a77367104)
![image](https://github.com/user-attachments/assets/055f3342-fe60-4164-a588-4bf3175a714e)
![image](https://github.com/user-attachments/assets/b0b8cc2b-579f-4fd0-b8ca-b8a61c37ca7f)
![image](https://github.com/user-attachments/assets/8504cbd0-18b4-4f6c-ace9-4b7ed269b02a)
![image](https://github.com/user-attachments/assets/6f9ab564-738d-4b51-b70a-be32a1ae0992)
![image](https://github.com/user-attachments/assets/cabb2d74-f1c3-4efe-812b-17d58c4cb775)
![image](https://github.com/user-attachments/assets/67d6557a-e2dc-4f98-9152-fcc58e379eb3)









