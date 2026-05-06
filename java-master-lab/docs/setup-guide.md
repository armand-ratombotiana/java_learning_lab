# Java Master Lab - Setup Guide

Complete step-by-step guide to set up and start learning with Java Master Lab.

## 📋 Prerequisites

- **Operating System**: Windows, macOS, or Linux
- **Internet Connection**: For downloading tools and dependencies
- **Administrator Access**: To install software
- **Disk Space**: At least 5GB for JDK, Maven, IDE, and projects

## 🚀 Quick Start (5 minutes)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/java-master-lab.git
cd java-master-lab
```

### 2. Run Setup Script

```bash
# On macOS/Linux
chmod +x scripts/setup-environment.sh
./scripts/setup-environment.sh

# On Windows (PowerShell)
.\scripts\setup-environment.ps1
```

### 3. Verify Installation

```bash
./scripts/verify-all.sh --quick
```

### 4. Start Learning

```bash
cd labs/00-environment-setup
cat README.md
```

---

## 📦 Detailed Installation Guide

### Step 1: Install Java Development Kit (JDK)

#### Windows

1. **Download JDK 17 LTS**
   - Visit [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/)
   - Select "JDK 17 LTS"
   - Choose Windows x64 installer (.exe)

2. **Run Installer**
   - Double-click the downloaded file
   - Click "Next" through the installation wizard
   - Accept the default installation path
   - Click "Finish"

3. **Verify Installation**
   ```bash
   java -version
   javac -version
   ```

4. **Set JAVA_HOME (Optional but Recommended)**
   - Right-click "This PC" → Properties
   - Click "Advanced system settings"
   - Click "Environment Variables"
   - Click "New" under System variables
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-17` (or your installation path)
   - Click OK

#### macOS

**Using Homebrew (Recommended)**:
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install JDK 17
brew install openjdk@17

# Set JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# Verify
java -version
```

**Manual Installation**:
1. Download from [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/)
2. Run the installer
3. Set JAVA_HOME in `~/.zshrc` or `~/.bash_profile`

#### Linux (Ubuntu/Debian)

```bash
# Update package manager
sudo apt update

# Install JDK 17
sudo apt install openjdk-17-jdk

# Verify
java -version
javac -version

# Set JAVA_HOME (optional)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### Step 2: Install Maven

#### Windows

1. **Download Maven**
   - Visit [Maven Download](https://maven.apache.org/download.cgi)
   - Download "Binary zip archive"

2. **Extract Archive**
   - Extract to `C:\Program Files\Apache\maven` (or preferred location)

3. **Set Environment Variables**
   - Right-click "This PC" → Properties
   - Click "Advanced system settings"
   - Click "Environment Variables"
   - Click "New" under System variables
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\maven`
   - Click OK
   - Edit `PATH` variable, add: `%MAVEN_HOME%\bin`

4. **Verify**
   ```bash
   mvn -version
   ```

#### macOS

```bash
# Using Homebrew
brew install maven

# Verify
mvn -version
```

#### Linux

```bash
# Ubuntu/Debian
sudo apt install maven

# Verify
mvn -version
```

### Step 3: Install an IDE

#### IntelliJ IDEA Community Edition (Recommended)

1. **Download**
   - Visit [IntelliJ IDEA Download](https://www.jetbrains.com/idea/download/)
   - Download Community Edition

2. **Install**
   - Run the installer
   - Accept default settings
   - Launch IntelliJ IDEA

3. **Configure JDK**
   - File → Project Structure → SDKs
   - Click "+" → Add JDK
   - Select your JDK 17 installation
   - Click OK

#### VS Code

1. **Download**
   - Visit [VS Code Download](https://code.visualstudio.com/)

2. **Install Extensions**
   - Open VS Code
   - Go to Extensions (Ctrl+Shift+X)
   - Search and install:
     - "Extension Pack for Java" (Microsoft)
     - "Maven for Java" (Microsoft)

3. **Configure JDK**
   - Open Command Palette (Ctrl+Shift+P)
   - Search "Java: Configure Runtime"
   - Select JDK 17

#### Eclipse

1. **Download**
   - Visit [Eclipse Download](https://www.eclipse.org/downloads/)
   - Download Eclipse IDE for Java Developers

2. **Install**
   - Extract and run eclipse.exe (Windows) or eclipse (macOS/Linux)

3. **Configure JDK**
   - Window → Preferences → Java → Installed JREs
   - Click "Add"
   - Select JDK 17 installation
   - Click OK

### Step 4: Install Git

#### Windows

1. **Download**
   - Visit [Git Download](https://git-scm.com/download/win)

2. **Run Installer**
   - Accept default settings
   - Choose "Use Git from Git Bash only" or "Use Git from Windows Command Prompt"
   - Click Finish

3. **Verify**
   ```bash
   git --version
   ```

#### macOS

```bash
# Using Homebrew
brew install git

# Verify
git --version
```

#### Linux

```bash
# Ubuntu/Debian
sudo apt install git

# Verify
git --version
```

### Step 5: Configure Git (Optional but Recommended)

```bash
# Set your name
git config --global user.name "Your Name"

# Set your email
git config --global user.email "your.email@example.com"

# Verify configuration
git config --global --list
```

---

## 🔧 Verify Your Setup

### Quick Verification

```bash
# Check Java
java -version
javac -version

# Check Maven
mvn -version

# Check Git
git --version
```

### Run Verification Script

```bash
# Make script executable (macOS/Linux)
chmod +x scripts/verify-all.sh

# Run verification
./scripts/verify-all.sh --quick
```

### Expected Output

```
[INFO] ================================
[INFO] Java Master Lab - Verification Script
[INFO] ================================

[INFO] --- Verifying Java Installation ---
[SUCCESS] Found: java version "17.0.x"
[SUCCESS] Java version is 17 or higher

[INFO] --- Verifying Maven Installation ---
[SUCCESS] Found: Apache Maven 3.8.x

[INFO] --- Verifying Git Installation ---
[SUCCESS] Found: git version 2.x.x

[SUCCESS] All environment requirements met
```

---

## 📂 Project Structure

```
java-master-lab/
├── labs/                          # All learning labs
│   ├── 00-environment-setup/      # Environment setup
│   ├── 01-java-basics/            # Java basics
│   ├── 02-operators-control-flow/ # Operators and control flow
│   └── ... (50+ labs)
├── scripts/                       # Automation scripts
│   ├── verify-all.sh             # Verification script
│   └── setup-environment.sh       # Setup script
├── templates/                     # Project templates
│   ├── LAB_TEMPLATE.md           # Lab template
│   ├── maven-starter/            # Maven template
│   └── gradle-starter/           # Gradle template
├── docs/                          # Documentation
│   ├── roadmap.md                # Curriculum roadmap
│   ├── best-practices.md         # Best practices guide
│   ├── setup-guide.md            # This file
│   └── faq.md                    # FAQ
├── .github/                       # GitHub configuration
│   └── workflows/                # CI/CD workflows
├── README.md                      # Main README
├── CONTRIBUTING.md               # Contribution guidelines
├── LICENSE                       # MIT License
└── pom.xml                       # Root Maven configuration
```

---

## 🎯 First Steps

### 1. Navigate to Lab 00

```bash
cd labs/00-environment-setup
```

### 2. Read the Lab

```bash
# On macOS/Linux
cat README.md

# On Windows
type README.md
```

### 3. Create Your First Project

```bash
# Create a new Maven project
mvn archetype:generate \
  -DgroupId=com.learning \
  -DartifactId=hello-world \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

# Navigate to project
cd hello-world

# Build
mvn clean install

# Run
mvn exec:java -Dexec.mainClass="com.learning.App"
```

### 4. Open in IDE

**IntelliJ IDEA**:
- File → Open
- Select the project folder
- Click OK

**VS Code**:
- File → Open Folder
- Select the project folder

**Eclipse**:
- File → Import
- Select "Existing Maven Projects"
- Browse to project folder

---

## 🐛 Troubleshooting

### Java Not Found

**Problem**: `java: command not found`

**Solution**:
1. Verify Java is installed: `java -version`
2. Set JAVA_HOME environment variable
3. Add `$JAVA_HOME/bin` to PATH

### Maven Not Found

**Problem**: `mvn: command not found`

**Solution**:
1. Verify Maven is installed: `mvn -version`
2. Set MAVEN_HOME environment variable
3. Add `$MAVEN_HOME/bin` to PATH

### Build Fails

**Problem**: `BUILD FAILURE`

**Solution**:
```bash
# Clear Maven cache
mvn clean

# Rebuild
mvn install

# Check for errors
mvn install -X  # Verbose output
```

### IDE Can't Find JDK

**Problem**: IDE shows "JDK not configured"

**Solution**:
1. Go to IDE settings
2. Find Java/JDK configuration
3. Point to JDK 17 installation directory
4. Restart IDE

### Port Already in Use

**Problem**: `Address already in use`

**Solution**:
```bash
# Find process using port (macOS/Linux)
lsof -i :8080

# Kill process
kill -9 <PID>

# On Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

## 📚 Next Steps

1. **Complete Lab 00**: Environment Setup
2. **Start Lab 01**: Java Basics
3. **Follow the Curriculum**: Progress through phases
4. **Build Projects**: Create portfolio-ready applications
5. **Share Your Work**: Add projects to GitHub

---

## 🆘 Getting Help

### Documentation
- [Java Documentation](https://docs.oracle.com/en/java/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Git Documentation](https://git-scm.com/doc)

### Community
- GitHub Issues: Report problems
- GitHub Discussions: Ask questions
- Stack Overflow: Search for solutions

### Resources
- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Maven Tutorial](https://maven.apache.org/guides/getting-started/)
- [Git Tutorial](https://git-scm.com/book/en/v2)

---

## ✅ Setup Checklist

- [ ] Java 17+ installed and verified
- [ ] Maven installed and verified
- [ ] IDE installed and configured
- [ ] Git installed and configured
- [ ] Repository cloned
- [ ] Verification script passed
- [ ] First project created
- [ ] IDE opens projects correctly
- [ ] Ready to start Lab 00

---

**Congratulations! Your environment is ready. Start with [Lab 00: Environment Setup](../labs/00-environment-setup/README.md)!**