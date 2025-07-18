# Streamlyze Backend API 🚀

A powerful Spring Boot backend that provides comprehensive APIs for streaming service analytics, web crawling, and advanced text processing. This server implements sophisticated algorithms including AVL trees, Trie data structures, and edit distance calculations.

> **Note**: This is the backend repository. The React frontend application can be found at: [Streamlyze Frontend Repository](https://github.com/kunal070/Streaming-Plan-Recommendation-frontend)

## 🏗️ Architecture Overview

This backend serves as the data processing engine for the Streamlyze platform, offering:
- RESTful APIs for streaming service data
- Advanced web crawling capabilities
- Text processing and analysis algorithms
- Real-time data scraping with Selenium
- File processing and data extraction

## 🚀 Key Features

### 📊 Streaming Service APIs
- **Live Data Scraping**: Real-time Netflix plan extraction using Selenium WebDriver
- **CSV-Based Data**: Efficient plan comparison across multiple streaming platforms
- **Smart Recommendations**: Algorithm-driven plan suggestions based on user preferences

### 🕷️ Advanced Web Crawling
- **Intelligent Crawler**: Configurable depth web crawling with JSoup integration
- **Content Analysis**: Extract and analyze text content from crawled pages
- **URL Management**: Automatic discovery and storage of linked pages

### 🔍 Search & Text Processing Algorithms
- **AVL Trees**: Self-balancing binary search trees for O(log n) keyword frequency counting
- **Trie Implementation**: Prefix trees for fast inverted indexing and search
- **Edit Distance**: Levenshtein distance algorithm for spell checking
- **Hash Maps**: Efficient word frequency tracking

### 📁 File Processing
- **Text Analysis**: Extract URLs, emails, phone numbers, and dates using regex patterns
- **Multi-format Support**: Process uploaded text files with comprehensive data extraction

## 🛠️ Tech Stack

### Core Framework
- **Spring Boot 3.3.5** - Modern Java-based backend framework
- **Spring Web** - RESTful web services
- **Spring DevTools** - Development productivity tools

### Web Scraping & Processing
- **Selenium WebDriver 4.26.0** - Browser automation for live data scraping
- **WebDriverManager 5.5.3** - Automatic driver management
- **JSoup 1.18.1** - HTML parsing and manipulation

### Data Processing
- **Apache Commons CSV 1.10.0** - CSV file processing
- **Jackson Databind** - JSON data binding and serialization

### Algorithms & Data Structures
- **Custom AVL Tree** - Self-balancing BST implementation
- **Trie Data Structure** - Prefix tree for efficient string operations
- **Edit Distance** - Dynamic programming implementation
- **Hash Maps** - Built-in Java collections for frequency counting

## 📦 Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Chrome browser (for Selenium WebDriver)

### Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/streamlyze-backend.git
   cd streamlyze-backend
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Add required data files**
   ```bash
   # Place these files in the root directory:
   # - Combined.csv (streaming plans data)
   # - words.txt (dictionary for spell checking)
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Verify installation**
   Server will start on `http://localhost:8080`
   Test with: `GET http://localhost:8080/api/netflix/plans`

### Frontend Integration
This backend is designed to work with the Streamlyze React frontend:
[Streamlyze Frontend Repository](https://github.com/kunal070/Streaming-Plan-Recommendation-frontend)

## 🏗️ Project Structure

```
src/main/java/com/advance/real/
├── controller/                  # REST API endpoints
│   ├── NetflixPlansController.java    # Netflix live scraping
│   ├── BestPlansController.java       # Plan recommendation logic
│   ├── StreamingPlansController.java  # CSV-based plan APIs
│   ├── WebCrawlerController.java      # Web crawling & algorithms
│   ├── universal.java                 # File processing & utilities
│   ├── PlanDTO.java                   # Data transfer objects
│   ├── CrawlRequest.java              # Request models
│   └── WebConfig.java                 # CORS configuration
├── services/                    # Business logic
│   └── OttScraperService.java         # Spell checking algorithms
├── RealApplication.java         # Main Spring Boot application
└── resources/
    └── application.properties   # Configuration settings
```

## 🔧 API Documentation

### Plan Management APIs
```bash
# Get live Netflix plans (Selenium scraping)
GET /api/netflix/plans

# Get streaming plans by service (CSV data)
GET /api/Disney/plans
GET /api/Prime/plans
GET /api/Paramount/plans
GET /api/YouTube/plans
GET /api/Discovery/plans

# Get best plan recommendations
GET /api/best/price          # Best plan by price
GET /api/best/videoquality   # Best plan by video quality
```

### Web Crawling APIs
```bash
# Start web crawling
POST /crawl
Body: {"url": "https://example.com"}

# Keyword frequency using AVL trees
GET /crawl/frequency-count?keyword=example

# Keyword frequency in crawled pages
GET /api/netflix/keyword-frequency?keyword=example
```

### Search & Text Processing APIs
```bash
# Spell checking with edit distance
GET /crawl/words?word=misspelled
GET /crawl/spell-checking?word=misspelled

# Inverted indexing with Trie
GET /crawl/inverted-index?keyword=search

# Page ranking algorithm
POST /api/page-ranking?word=keyword

# Search frequency tracking
GET /api/search-frequency?word=keyword

# File analysis and data extraction
POST /api/analyze-file
Content-Type: multipart/form-data
Body: file (text file)
```

## 🎯 Algorithm Implementations

### AVL Tree (Keyword Frequency)
```java
// Self-balancing binary search tree for O(log n) operations
class AVLNode {
    String word;
    int count;
    int height;
    AVLNode left, right;
}
```
**Use Case**: Efficient keyword frequency counting in web crawling
**Complexity**: O(log n) insert, search, delete

### Trie (Inverted Indexing)
```java
// Prefix tree for fast pattern matching
class TrieNode {
    Map<Character, TrieNode> children;
    List<Position> positions;
}
```
**Use Case**: Fast text search and inverted indexing
**Complexity**: O(m) where m is the length of the search term

### Edit Distance (Spell Checking)
```java
// Levenshtein distance for word similarity
public static int calculateEditDistance(String word1, String word2)
```
**Use Case**: Spell correction and word suggestions
**Complexity**: O(n × m) dynamic programming solution

## 📁 Required Files

### Combined.csv
Contains streaming service plan data with columns:
- plan, price, ad-supported, videoQuality, spatialAudio
- watchDevice, supportedDownloadDevices, streamingService, link

### words.txt
Dictionary file for spell checking functionality
- One word per line
- Used for edit distance calculations
- Required for spell checking APIs

## 🔧 Configuration

### Application Properties
```properties
spring.application.name=real
server.port=8080
spring.devtools.restart.enabled=true
logging.level.org.springframework.web=DEBUG
```

### CORS Configuration
Configured to allow all origins for development:
```java
config.addAllowedOriginPattern("*");
config.addAllowedMethod("*");
config.addAllowedHeader("*");
```

## 🚀 Performance Features

### Caching & Optimization
- **File Caching**: Crawled content stored locally for repeated analysis
- **Efficient Data Structures**: AVL trees and Tries for optimal performance
- **Batch Processing**: Handle multiple files simultaneously

### Scalability
- **Configurable Limits**: Adjustable crawling depth and page limits
- **Memory Management**: Efficient handling of large text files
- **Error Handling**: Robust exception handling for web scraping

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Implement your changes with proper tests
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related Repositories

- **Frontend Application**: [Streamlyze Frontend](https://github.com/kunal070/Streaming-Plan-Recommendation-frontend)

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- Selenium project for web automation capabilities
- Apache Commons for CSV processing utilities
- Algorithm implementations inspired by classic CS textbooks

---

**Note**: This backend server must be running on port 8080 for the frontend application to function properly. Ensure Chrome browser is installed for Selenium web scraping features.
