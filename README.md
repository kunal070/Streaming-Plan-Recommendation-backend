# Streamlyze 🎬

A full-stack streaming service analytics platform that combines React frontend with Spring Boot backend to help users compare streaming plans, analyze web content, and perform advanced text processing operations.

## 🚀 Features

### 📊 Plan Comparison & Analytics
- **Multi-Platform Comparison**: Compare Netflix, Prime Video, Disney+, Discovery+, Paramount+, and YouTube plans
- **Smart Recommendations**: Get AI-powered plan suggestions based on price or video quality preferences
- **Real-time Scraping**: Live data extraction from streaming service websites using Selenium

### 🕷️ Advanced Web Crawling
- **Intelligent Web Crawler**: Extract and analyze content from websites with configurable depth limits
- **Keyword Frequency Analysis**: Count keyword occurrences across crawled pages using AVL trees
- **URL Discovery**: Automatically discover and save linked pages for comprehensive analysis

### 🔍 Search & Text Processing
- **Inverted Indexing**: Fast search capabilities using Trie data structures
- **Spell Checking**: Edit distance-based spell correction with word suggestions
- **Text Extraction**: Extract URLs, emails, phone numbers, and dates from uploaded files
- **Page Ranking**: Rank pages by keyword relevance using custom algorithms

### 🛠️ Technical Features
- **Data Structures**: Implementation of AVL trees, Tries, and Hash maps for efficient data processing
- **File Processing**: Handle text file uploads and content analysis
- **RESTful APIs**: Comprehensive backend API with proper CORS configuration
- **Responsive UI**: Modern React interface with loading states and error handling

## 🏗️ Tech Stack

### Frontend
- **React.js** - Component-based UI framework
- **React Router** - Client-side routing
- **Axios** - HTTP client for API communication
- **CSS3** - Custom styling with animations

### Backend
- **Spring Boot 3.3.5** - Java-based backend framework
- **Selenium WebDriver** - Web scraping and automation
- **JSoup** - HTML parsing and manipulation
- **Apache Commons CSV** - CSV file processing
- **Jackson** - JSON data binding

### Data Structures & Algorithms
- **AVL Trees** - Self-balancing binary search trees for word frequency
- **Trie** - Prefix trees for inverted indexing
- **Hash Maps** - Fast keyword frequency tracking
- **Edit Distance** - Levenshtein distance for spell checking

## 📦 Installation

### Prerequisites
- Java 17 or higher
- Node.js 14+ and npm
- Chrome browser (for Selenium)

### Backend Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/streamlyze.git
   cd streamlyze
   ```

2. **Install backend dependencies**
   ```bash
   mvn clean install
   ```

3. **Add required files**
   - Place `Combined.csv` in the root directory (contains streaming plan data)
   - Add `words.txt` file for spell checking functionality

4. **Run the Spring Boot application**
   ```bash
   mvn spring-boot:run
   ```
   Server will start on `http://localhost:8080`

### Frontend Setup
1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```
   Application will open at `http://localhost:3000`

## 📝 Usage Guide

### Plan Comparison
1. Navigate to Netflix, Prime, or Disney+ sections
2. View real-time scraped plan data with pricing and features
3. Use "Best Plan" feature to get personalized recommendations

### Web Crawling & Analysis
1. **Web Crawler**: Enter a URL to crawl up to 10 linked pages
2. **Keyword Analysis**: Search for specific terms across crawled content
3. **Frequency Counting**: Get detailed keyword frequency reports

### Advanced Search Features
1. **Page Ranking**: Search and rank pages by keyword relevance
2. **Inverted Indexing**: Fast search with position-based results
3. **Spell Checking**: Get word suggestions for misspelled queries

### Text Processing
1. Upload `.txt` files for analysis
2. Extract structured data (URLs, emails, phone numbers, dates)
3. View organized results in separate categories

## 🏗️ Project Structure

```
streamlyze/
├── src/main/java/com/advance/real/
│   ├── controller/           # REST API endpoints
│   │   ├── NetflixPlansController.java
│   │   ├── BestPlansController.java
│   │   ├── WebCrawlerController.java
│   │   └── universal.java
│   ├── services/            # Business logic services
│   │   └── OttScraperService.java
│   └── RealApplication.java # Main Spring Boot application
├── frontend/src/
│   ├── components/          # React components
│   │   ├── Netflix.js
│   │   ├── BestPlan.js
│   │   ├── Web.js
│   │   ├── Page.js
│   │   └── Invert.js
│   ├── styles/             # CSS stylesheets
│   └── App.js              # Main React app
├── Combined.csv            # Streaming plans database
├── words.txt              # Dictionary for spell checking
└── pom.xml                # Maven dependencies
```

## 🔧 API Endpoints

### Plan Management
- `GET /api/netflix/plans` - Get Netflix plans (live scraping)
- `GET /api/Disney/plans` - Get Disney+ plans from CSV
- `GET /api/Prime/plans` - Get Prime Video plans from CSV
- `GET /api/best/price` - Get best plan by price
- `GET /api/best/videoquality` - Get best plan by video quality

### Web Crawling
- `POST /crawl` - Start web crawling process
- `GET /crawl/frequency-count` - Get keyword frequency using AVL trees
- `GET /api/netflix/keyword-frequency` - Analyze keyword frequency in crawled pages

### Search & Text Processing
- `GET /crawl/words` - Get spell-check suggestions
- `GET /crawl/inverted-index` - Search using inverted index (Trie)
- `POST /api/page-ranking` - Rank pages by keyword relevance
- `POST /api/analyze-file` - Extract data from uploaded text files

## 🎯 Key Algorithms

| Algorithm | Use Case | Implementation |
|-----------|----------|----------------|
| **AVL Trees** | Keyword frequency counting | Self-balancing BST for O(log n) operations |
| **Trie** | Inverted indexing | Prefix trees for fast pattern matching |
| **Edit Distance** | Spell checking | Levenshtein distance for word similarity |
| **Hash Maps** | Frequency tracking | O(1) lookup for word counts |
| **Brute Force** | Page ranking | Linear search across file contents |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for excellent framework documentation
- React community for comprehensive guides
- Selenium project for web automation capabilities
- Apache Commons for CSV processing utilities

---

**Note**: Ensure Chrome browser is installed for web scraping functionality. The application requires both frontend and backend servers to be running simultaneously for full functionality.
