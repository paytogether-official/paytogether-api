# Paytogether API

## Getting Started

### Environment Variables

To run this project, you will need to set the following environment variables:

```bash
$ cp .env.example .env
```

Then, fill in the `.env` file with the required values.

**Database Configuration**
- `DATABASE_HOST`: The host of your database.
- `DATABASE_PORT`: The port of your database.
- `DATABASE_NAME`: The name of your database.
- `DATABASE_USERNAME`: The username to connect to your database.
- `DATABASE_PASSWORD`: The password to connect to your database.

**Financial Data API Configuration**
- `TWELVE_DATA_API_KEY`: The API key for Twelve Data.

### Docker
To run the application using Docker, you can use the following command:

```bash
$ docker pull ghcr.io/paytogether-official/paytogether-api:latest
$ docker run -d -p 8080:8080 --env-file .env ghcr.io/paytogether-official/paytogether-api:latest
```