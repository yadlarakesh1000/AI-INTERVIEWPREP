package com.interviewprep.modules.roadmap.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;
import com.interviewprep.modules.roadmap.repository.RoadmapTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds the handcrafted career roadmap templates (Section 7). Runs once on startup and only
 * if the table is empty. Each template's weekly plan is built from a curated list of weekly
 * topics; every week is broken into a consistent 7-day concept -> practice -> review cadence,
 * producing a complete plan for all 26 or 52 weeks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoadmapTemplateDataSeeder implements CommandLineRunner {

    private static final String[] DAY_PHASES = {
            "Concepts & Theory",
            "Deep Dive",
            "Hands-on Practice",
            "Worked Examples",
            "Guided Exercises",
            "Practice Problems",
            "Review & Quiz"
    };

    private final RoadmapTemplateRepository roadmapTemplateRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        if (roadmapTemplateRepository.count() > 0) {
            log.debug("Roadmap templates already seeded ({}); skipping", roadmapTemplateRepository.count());
            return;
        }

        List<RoadmapTemplate> templates = new ArrayList<>();
        templates.add(javaFullStack());
        templates.add(pythonFullStack());
        templates.add(aiMlEngineer());
        templates.add(dataAnalyst());
        templates.add(devOpsEngineer());
        templates.add(frontendReact());
        templates.add(backendJava());
        templates.add(mobileAndroid());
        templates.add(cloudAws());
        templates.add(cybersecurityAnalyst());
        templates.add(dataEngineer());
        templates.add(machineLearningEngineer());

        roadmapTemplateRepository.saveAll(templates);
        log.info("Seeded {} roadmap templates", templates.size());
    }

    // ============================================================================================
    // Roadmaps (26 weeks)
    // ============================================================================================

    private RoadmapTemplate javaFullStack() {
        String[] weeks = {
                "Java Fundamentals", "Object-Oriented Programming in Java", "Java Collections Framework",
                "Exception Handling & Generics", "Java Streams & Functional Programming",
                "Concurrency & Multithreading", "JDBC & Database Basics", "SQL & Relational Databases",
                "Maven & Build Tools", "Spring Core & Dependency Injection", "Spring Boot Fundamentals",
                "Building REST APIs with Spring Boot", "Spring Data JPA & Hibernate",
                "Spring Security & JWT Authentication", "Testing with JUnit & Mockito",
                "HTML, CSS & Responsive Design", "JavaScript Fundamentals", "Modern JavaScript (ES6+)",
                "React Fundamentals", "React Hooks & State Management", "Consuming REST APIs in React",
                "Full Stack Integration", "Docker & Containerization", "CI/CD & Deployment",
                "System Design Basics", "Capstone Project & Interview Prep"
        };
        return build(
                "Java Full Stack Developer",
                "Become a job-ready Java full stack developer. Master core Java, Spring Boot backend "
                        + "development, React frontend, and deployment — building real projects along the way.",
                "Java Full Stack Developer", weeks,
                List.of(
                        ms(5, "Core Java Mastery", "Comfortable with OOP, collections, and streams."),
                        ms(9, "Database Foundations", "Can model data and write SQL with JDBC."),
                        ms(15, "Backend Complete", "Built a secured, tested Spring Boot REST API."),
                        ms(21, "Frontend Complete", "Built a React SPA consuming REST APIs."),
                        ms(26, "Full Stack Portfolio", "Deployed a complete full stack application.")),
                List.of(
                        pr("Task Manager API", "A secured Spring Boot REST API with JWT auth and JPA."),
                        pr("E-Commerce Full Stack App", "React + Spring Boot store with cart and checkout."),
                        pr("Blog Platform", "Full stack blog with authentication and comments.")),
                List.of(
                        res("Oracle Java Documentation", "https://docs.oracle.com/en/java/", "documentation"),
                        res("Spring Boot Reference", "https://docs.spring.io/spring-boot/docs/current/reference/html/", "documentation"),
                        res("Baeldung Java & Spring Tutorials", "https://www.baeldung.com/", "article"),
                        res("React Documentation", "https://react.dev/", "documentation")));
    }

    private RoadmapTemplate pythonFullStack() {
        String[] weeks = {
                "Python Fundamentals", "Data Structures in Python", "Object-Oriented Python",
                "Functions, Modules & Packages", "File Handling & Exceptions", "Working with APIs & JSON",
                "SQL & Databases", "Python & Databases (SQLAlchemy)", "Flask Fundamentals",
                "Building REST APIs with Flask", "Django Fundamentals", "Django Models & ORM",
                "Django REST Framework", "Authentication & Authorization", "Testing in Python",
                "HTML, CSS & Responsive Design", "JavaScript Fundamentals", "Modern JavaScript (ES6+)",
                "React Fundamentals", "React State & Hooks", "Connecting React to Python APIs",
                "Full Stack Integration", "Docker & Containerization", "Deployment & DevOps Basics",
                "System Design Basics", "Capstone Project & Interview Prep"
        };
        return build(
                "Python Full Stack Developer",
                "Master Python full stack development with Flask and Django on the backend and React "
                        + "on the frontend, from fundamentals to deployment.",
                "Python Full Stack Developer", weeks,
                List.of(
                        ms(6, "Python Proficiency", "Solid grasp of Python, OOP, and APIs."),
                        ms(10, "Flask APIs", "Built REST APIs with Flask and SQLAlchemy."),
                        ms(15, "Django Mastery", "Built a secured, tested Django REST backend."),
                        ms(22, "Full Stack Integration", "Connected a React frontend to Python APIs."),
                        ms(26, "Deployed Portfolio", "Shipped a complete deployed application.")),
                List.of(
                        pr("Notes REST API", "A Flask + SQLAlchemy CRUD API with authentication."),
                        pr("Social Feed App", "Django REST + React app with user posts and likes."),
                        pr("Expense Tracker", "Full stack app with charts and reporting.")),
                List.of(
                        res("Python Documentation", "https://docs.python.org/3/", "documentation"),
                        res("Django Documentation", "https://docs.djangoproject.com/", "documentation"),
                        res("Flask Documentation", "https://flask.palletsprojects.com/", "documentation"),
                        res("Real Python Tutorials", "https://realpython.com/", "article")));
    }

    private RoadmapTemplate dataAnalyst() {
        String[] weeks = {
                "Introduction to Data Analytics", "Excel for Data Analysis", "Advanced Excel & Pivot Tables",
                "Statistics Fundamentals", "Descriptive & Inferential Statistics", "SQL Fundamentals",
                "Advanced SQL Queries", "Database Design Basics", "Python for Data Analysis",
                "NumPy Essentials", "Pandas for Data Wrangling", "Data Cleaning Techniques",
                "Exploratory Data Analysis", "Data Visualization with Matplotlib", "Visualization with Seaborn",
                "Introduction to Tableau", "Building Dashboards in Tableau", "Power BI Fundamentals",
                "DAX & Power BI Modeling", "Storytelling with Data", "A/B Testing & Experimentation",
                "Business Metrics & KPIs", "Case Study: Sales Analytics", "Case Study: Product Analytics",
                "Building a Portfolio", "Interview Prep & Capstone"
        };
        return build(
                "Data Analyst",
                "Learn to turn raw data into insights with Excel, SQL, Python, and BI tools like Tableau "
                        + "and Power BI — ready for a data analyst role.",
                "Data Analyst", weeks,
                List.of(
                        ms(5, "Stats & Spreadsheets", "Confident with statistics and advanced Excel."),
                        ms(8, "SQL Analyst", "Can query and model relational data fluently."),
                        ms(15, "Python Analytics", "Wrangling and visualizing data with pandas."),
                        ms(19, "BI Dashboards", "Built dashboards in Tableau and Power BI."),
                        ms(26, "Analyst Portfolio", "Completed end-to-end analytics case studies.")),
                List.of(
                        pr("Sales Dashboard", "An interactive Tableau/Power BI sales dashboard."),
                        pr("Customer Churn Analysis", "SQL + Python analysis of churn drivers."),
                        pr("Market Basket Analysis", "Product analytics on transaction data.")),
                List.of(
                        res("Mode SQL Tutorial", "https://mode.com/sql-tutorial/", "course"),
                        res("pandas Documentation", "https://pandas.pydata.org/docs/", "documentation"),
                        res("Tableau Public Resources", "https://public.tableau.com/app/resources", "course"),
                        res("Power BI Learning", "https://learn.microsoft.com/power-bi/", "documentation")));
    }

    private RoadmapTemplate devOpsEngineer() {
        String[] weeks = {
                "Linux Fundamentals", "Shell Scripting", "Networking Basics", "Git & Version Control",
                "CI/CD Concepts", "Jenkins Fundamentals", "Build Pipelines", "Docker Fundamentals",
                "Docker Compose & Networking", "Container Registries", "Kubernetes Fundamentals",
                "Kubernetes Deployments & Services", "Helm & Configuration", "Infrastructure as Code",
                "Terraform Fundamentals", "Ansible & Configuration Management", "AWS Core Services",
                "Cloud Networking & Security", "Monitoring with Prometheus", "Visualization with Grafana",
                "Centralized Logging (ELK)", "Site Reliability Engineering Basics", "Security & DevSecOps",
                "Scaling & High Availability", "Capstone: End-to-End Pipeline", "Interview Prep"
        };
        return build(
                "DevOps Engineer",
                "Master the DevOps toolchain: Linux, Docker, Kubernetes, Terraform, CI/CD, and monitoring "
                        + "to automate and operate reliable systems.",
                "DevOps Engineer", weeks,
                List.of(
                        ms(4, "Linux & Git", "Comfortable on the command line with version control."),
                        ms(10, "Containers", "Can build and ship Docker containers."),
                        ms(13, "Kubernetes", "Deploying and managing apps on Kubernetes."),
                        ms(16, "Infrastructure as Code", "Provisioning with Terraform and Ansible."),
                        ms(26, "Full Pipeline", "Built an end-to-end automated CI/CD pipeline.")),
                List.of(
                        pr("CI/CD Pipeline", "Jenkins pipeline building and deploying a containerized app."),
                        pr("Kubernetes Cluster", "Deploy a multi-service app to Kubernetes with Helm."),
                        pr("IaC Environment", "Provision cloud infrastructure with Terraform.")),
                List.of(
                        res("Docker Documentation", "https://docs.docker.com/", "documentation"),
                        res("Kubernetes Documentation", "https://kubernetes.io/docs/home/", "documentation"),
                        res("Terraform Documentation", "https://developer.hashicorp.com/terraform/docs", "documentation"),
                        res("Prometheus Documentation", "https://prometheus.io/docs/", "documentation")));
    }

    private RoadmapTemplate frontendReact() {
        String[] weeks = {
                "HTML Fundamentals", "CSS Fundamentals", "CSS Layout (Flexbox & Grid)",
                "Responsive & Mobile-First Design", "CSS Frameworks (Tailwind)", "JavaScript Fundamentals",
                "DOM Manipulation", "Modern JavaScript (ES6+)", "Asynchronous JavaScript & Promises",
                "Fetch API & AJAX", "Package Managers & Build Tools", "React Fundamentals",
                "JSX & Components", "Props & State", "React Hooks", "Handling Forms & Events",
                "React Router", "State Management (Context & Redux)", "Data Fetching & React Query",
                "Styling in React", "Testing React Apps", "Performance Optimization",
                "Accessibility & Best Practices", "TypeScript with React", "Deployment & Tooling",
                "Capstone Project & Interview Prep"
        };
        return build(
                "Frontend Developer (React)",
                "Build modern, responsive, accessible web interfaces with HTML, CSS, JavaScript, and React "
                        + "— including state management, testing, and TypeScript.",
                "Frontend Developer", weeks,
                List.of(
                        ms(5, "Styling Mastery", "Responsive layouts with Flexbox, Grid, and Tailwind."),
                        ms(11, "JavaScript Proficiency", "Comfortable with modern async JavaScript."),
                        ms(18, "React Core", "Built component-driven apps with hooks and routing."),
                        ms(23, "Production Ready", "Testing, performance, and accessibility covered."),
                        ms(26, "Deployed SPA", "Shipped a polished React application.")),
                List.of(
                        pr("Weather Dashboard", "A responsive React app consuming a weather API."),
                        pr("Kanban Board", "Drag-and-drop task board with state management."),
                        pr("E-Commerce Storefront", "React storefront with cart and TypeScript.")),
                List.of(
                        res("MDN Web Docs", "https://developer.mozilla.org/", "documentation"),
                        res("React Documentation", "https://react.dev/", "documentation"),
                        res("Tailwind CSS Docs", "https://tailwindcss.com/docs", "documentation"),
                        res("JavaScript.info", "https://javascript.info/", "course")));
    }

    private RoadmapTemplate backendJava() {
        String[] weeks = {
                "Java Fundamentals", "Object-Oriented Programming", "Collections & Generics",
                "Exception Handling", "Streams & Functional Programming", "Concurrency & Multithreading",
                "JVM & Memory Management", "SQL & Relational Databases", "JDBC", "Maven & Build Tools",
                "Spring Core & Dependency Injection", "Spring Boot Fundamentals", "REST API Design",
                "Building REST APIs with Spring Boot", "Spring Data JPA & Hibernate",
                "Spring Security & JWT", "Validation & Exception Handling", "Testing with JUnit & Mockito",
                "Caching & Performance", "Messaging with Kafka Basics", "Microservices Fundamentals",
                "API Documentation with Swagger", "Docker & Containerization", "CI/CD & Deployment",
                "System Design Basics", "Capstone Project & Interview Prep"
        };
        return build(
                "Backend Developer (Java)",
                "Go deep on server-side Java: core language mastery, Spring Boot, databases, security, "
                        + "testing, and an introduction to microservices.",
                "Backend Developer", weeks,
                List.of(
                        ms(7, "Advanced Java", "Concurrency and JVM internals understood."),
                        ms(10, "Data Layer", "SQL, JDBC, and build tooling covered."),
                        ms(18, "Spring Boot APIs", "Secured, validated, tested REST services."),
                        ms(22, "Distributed Basics", "Introduced to messaging and microservices."),
                        ms(26, "Backend Portfolio", "Deployed a production-style backend service.")),
                List.of(
                        pr("Banking API", "A transactional Spring Boot API with strong validation."),
                        pr("Inventory Microservice", "Service with Kafka events and Swagger docs."),
                        pr("URL Shortener", "High-throughput API with caching.")),
                List.of(
                        res("Spring Framework Docs", "https://docs.spring.io/spring-framework/reference/", "documentation"),
                        res("Baeldung Tutorials", "https://www.baeldung.com/", "article"),
                        res("Hibernate ORM Docs", "https://hibernate.org/orm/documentation/", "documentation"),
                        res("Apache Kafka Docs", "https://kafka.apache.org/documentation/", "documentation")));
    }

    private RoadmapTemplate mobileAndroid() {
        String[] weeks = {
                "Kotlin Fundamentals", "Kotlin OOP & Functions", "Android Studio & Project Setup",
                "Android App Components", "Activities & Lifecycle", "Layouts & Views",
                "RecyclerView & Lists", "Fragments & Navigation", "Jetpack Navigation Component",
                "ViewModel & LiveData", "Jetpack Compose Basics", "State & UI in Compose",
                "Networking with Retrofit", "JSON Parsing & APIs", "Local Storage with Room",
                "Coroutines & Async", "Dependency Injection with Hilt", "Material Design",
                "Permissions & Sensors", "Background Work & WorkManager", "Push Notifications (FCM)",
                "Testing Android Apps", "Publishing to Play Store", "Firebase Integration",
                "Capstone Project", "Interview Prep"
        };
        return build(
                "Mobile Developer (Android)",
                "Build native Android apps with Kotlin and Jetpack Compose — covering UI, networking, "
                        + "local storage, and publishing to the Play Store.",
                "Mobile Developer", weeks,
                List.of(
                        ms(2, "Kotlin Ready", "Comfortable writing idiomatic Kotlin."),
                        ms(10, "Android Core", "Activities, fragments, and architecture components."),
                        ms(12, "Jetpack Compose", "Building modern declarative UIs."),
                        ms(17, "Data & DI", "Networking, Room, coroutines, and Hilt."),
                        ms(26, "Published App", "Shipped an app to the Play Store.")),
                List.of(
                        pr("Weather App", "Compose app consuming a REST weather API."),
                        pr("Notes App", "Offline-first notes with Room and coroutines."),
                        pr("Chat App", "Realtime chat with Firebase.")),
                List.of(
                        res("Android Developers", "https://developer.android.com/", "documentation"),
                        res("Kotlin Documentation", "https://kotlinlang.org/docs/home.html", "documentation"),
                        res("Jetpack Compose Docs", "https://developer.android.com/jetpack/compose", "documentation"),
                        res("Retrofit Documentation", "https://square.github.io/retrofit/", "documentation")));
    }

    private RoadmapTemplate dataEngineer() {
        String[] weeks = {
                "Python for Data Engineering", "SQL Fundamentals", "Advanced SQL", "Data Modeling",
                "Relational Databases Deep Dive", "NoSQL Databases", "Data Warehousing Concepts",
                "ETL vs ELT", "Building ETL Pipelines", "Apache Airflow Fundamentals",
                "Orchestrating Workflows", "Batch Processing", "Apache Spark Fundamentals",
                "Spark SQL & DataFrames", "Stream Processing Basics", "Apache Kafka Fundamentals",
                "Kafka Streams", "Cloud Data Platforms", "Data Lakes", "dbt & Analytics Engineering",
                "Data Quality & Testing", "Data Governance", "Big Data on Cloud",
                "CI/CD for Data Pipelines", "Capstone: End-to-End Pipeline", "Interview Prep"
        };
        return build(
                "Data Engineer",
                "Design and build data pipelines and platforms with SQL, Airflow, Spark, and Kafka — "
                        + "from ingestion to warehousing on the cloud.",
                "Data Engineer", weeks,
                List.of(
                        ms(5, "Data Modeling", "Relational modeling and advanced SQL."),
                        ms(11, "Orchestration", "Building ETL pipelines with Airflow."),
                        ms(14, "Big Data", "Processing at scale with Spark."),
                        ms(17, "Streaming", "Real-time pipelines with Kafka."),
                        ms(26, "Data Platform", "Built an end-to-end cloud data pipeline.")),
                List.of(
                        pr("ETL Pipeline", "Airflow-orchestrated pipeline into a warehouse."),
                        pr("Streaming Analytics", "Kafka + Spark streaming aggregation."),
                        pr("Analytics Warehouse", "dbt-modeled warehouse with data tests.")),
                List.of(
                        res("Apache Airflow Docs", "https://airflow.apache.org/docs/", "documentation"),
                        res("Apache Spark Docs", "https://spark.apache.org/docs/latest/", "documentation"),
                        res("Apache Kafka Docs", "https://kafka.apache.org/documentation/", "documentation"),
                        res("dbt Documentation", "https://docs.getdbt.com/", "documentation")));
    }

    // ============================================================================================
    // Roadmaps (52 weeks)
    // ============================================================================================

    private RoadmapTemplate aiMlEngineer() {
        String[] weeks = {
                "Python for Data Science", "NumPy & Array Computing", "Pandas & Data Manipulation",
                "Data Visualization (Matplotlib/Seaborn)", "Statistics Fundamentals", "Probability for ML",
                "Linear Algebra for ML", "Calculus for ML", "Data Cleaning & Preprocessing",
                "Exploratory Data Analysis", "Introduction to Machine Learning", "Linear Regression",
                "Logistic Regression", "Decision Trees & Random Forests", "Support Vector Machines",
                "K-Nearest Neighbors & Naive Bayes", "Model Evaluation & Metrics",
                "Cross-Validation & Hyperparameter Tuning", "Feature Engineering",
                "Ensemble Methods & Boosting", "Unsupervised Learning: Clustering",
                "Dimensionality Reduction (PCA)", "Introduction to Neural Networks",
                "Deep Learning Fundamentals", "Backpropagation & Optimization", "TensorFlow & Keras Basics",
                "PyTorch Basics", "Convolutional Neural Networks", "Image Classification Projects",
                "Transfer Learning", "Recurrent Neural Networks", "LSTMs & Sequence Models",
                "Natural Language Processing Basics", "Text Preprocessing & Embeddings",
                "Transformers & Attention", "Working with Pretrained Language Models",
                "Model Deployment Basics", "Building ML APIs with FastAPI", "MLOps Fundamentals",
                "Experiment Tracking & Versioning", "Docker for ML", "Cloud ML Platforms",
                "Big Data Basics (Spark)", "Recommender Systems", "Time Series Forecasting",
                "Reinforcement Learning Basics", "Generative Models Overview", "Responsible AI & Ethics",
                "ML System Design", "Capstone Project I", "Capstone Project II", "Portfolio & Interview Prep"
        };
        return build(
                "AI/ML Engineer",
                "A full year from Python and math foundations through classical ML, deep learning, NLP, "
                        + "and deployment — everything to become an AI/ML engineer.",
                "AI Engineer", weeks,
                List.of(
                        ms(10, "Foundations", "Python, math, and data analysis foundations."),
                        ms(22, "Classical ML", "Confident with supervised and unsupervised ML."),
                        ms(36, "Deep Learning & NLP", "Neural networks, CNNs, RNNs, and transformers."),
                        ms(43, "Deployment & MLOps", "Serving and operating models in production."),
                        ms(52, "AI Portfolio", "Completed capstone projects and interview prep.")),
                List.of(
                        pr("Image Classifier", "A CNN image classifier with transfer learning."),
                        pr("Sentiment Analyzer", "An NLP model served via a FastAPI endpoint."),
                        pr("Recommender System", "A collaborative-filtering recommendation engine.")),
                List.of(
                        res("scikit-learn Documentation", "https://scikit-learn.org/stable/", "documentation"),
                        res("TensorFlow Tutorials", "https://www.tensorflow.org/tutorials", "course"),
                        res("PyTorch Tutorials", "https://pytorch.org/tutorials/", "course"),
                        res("Hugging Face Course", "https://huggingface.co/learn", "course")));
    }

    private RoadmapTemplate cloudAws() {
        String[] weeks = {
                "Cloud Computing Fundamentals", "AWS Account & IAM", "IAM Deep Dive & Security",
                "EC2 Fundamentals", "EC2 Networking & Storage", "VPC Fundamentals",
                "VPC Advanced Networking", "S3 Fundamentals", "S3 Security & Lifecycle",
                "EBS & EFS Storage", "Route 53 & DNS", "Elastic Load Balancing", "Auto Scaling",
                "RDS Fundamentals", "DynamoDB Basics", "Database Migration", "Lambda & Serverless",
                "API Gateway", "Serverless Applications", "SQS & SNS Messaging", "CloudFormation Basics",
                "Infrastructure as Code", "CloudWatch Monitoring", "CloudTrail & Config",
                "Elastic Beanstalk", "ECS & Containers", "EKS & Kubernetes on AWS",
                "ECR & Container Registry", "CI/CD with CodePipeline", "CodeBuild & CodeDeploy",
                "Security Best Practices", "KMS & Encryption", "WAF & Shield", "Cost Optimization",
                "Well-Architected Framework", "High Availability Design", "Disaster Recovery",
                "Caching with CloudFront", "ElastiCache", "Data Analytics on AWS", "Kinesis & Streaming",
                "Athena & Glue", "Step Functions", "EventBridge", "Systems Manager",
                "Multi-Account Strategy", "Advanced Networking (Transit Gateway)", "Migration Strategies",
                "Terraform on AWS", "Solutions Architect Prep I", "Solutions Architect Prep II",
                "Capstone & Interview Prep"
        };
        return build(
                "Cloud Engineer (AWS)",
                "A comprehensive year on Amazon Web Services: compute, networking, storage, serverless, "
                        + "containers, security, and architecture — aligned to the Solutions Architect path.",
                "Cloud Engineer", weeks,
                List.of(
                        ms(13, "Core Infrastructure", "EC2, VPC, S3, and load balancing mastered."),
                        ms(24, "Serverless & IaC", "Lambda, API Gateway, and CloudFormation."),
                        ms(37, "Well-Architected", "Security, HA, and disaster recovery design."),
                        ms(48, "Advanced AWS", "Analytics, migration, and multi-account setups."),
                        ms(52, "Certified & Ready", "Architected a full solution and prepped for certification.")),
                List.of(
                        pr("Highly Available Web App", "Auto-scaled app across multiple AZs."),
                        pr("Serverless API", "Lambda + API Gateway + DynamoDB backend."),
                        pr("IaC Deployment", "Full environment provisioned with Terraform.")),
                List.of(
                        res("AWS Documentation", "https://docs.aws.amazon.com/", "documentation"),
                        res("AWS Well-Architected", "https://aws.amazon.com/architecture/well-architected/", "article"),
                        res("AWS Skill Builder", "https://skillbuilder.aws/", "course"),
                        res("Terraform AWS Provider", "https://registry.terraform.io/providers/hashicorp/aws/latest/docs", "documentation")));
    }

    private RoadmapTemplate cybersecurityAnalyst() {
        String[] weeks = {
                "Introduction to Cybersecurity", "Security Principles & CIA Triad", "Networking Fundamentals",
                "Network Protocols & TCP/IP", "Linux Fundamentals for Security", "Windows Security Basics",
                "Command Line & Scripting", "Cryptography Fundamentals", "Encryption & Hashing",
                "Public Key Infrastructure", "Authentication & Access Control", "Identity Management",
                "Threats & Vulnerabilities", "Malware Types & Analysis Basics", "Social Engineering",
                "Security Policies & Governance", "Risk Management", "Compliance & Frameworks",
                "Network Security", "Firewalls & IDS/IPS", "VPNs & Secure Communication",
                "Wireless Security", "Web Application Security", "OWASP Top 10", "SQL Injection & XSS",
                "Secure Coding Practices", "Penetration Testing Basics", "Reconnaissance & Scanning",
                "Vulnerability Assessment", "Exploitation Fundamentals", "Metasploit Basics",
                "Post-Exploitation", "Reporting & Documentation", "Security Operations Center (SOC)",
                "SIEM Fundamentals", "Log Analysis", "Incident Response", "Digital Forensics Basics",
                "Threat Intelligence", "Threat Hunting", "Endpoint Security", "Cloud Security Fundamentals",
                "Container & DevSecOps", "IoT Security", "Mobile Security", "Security Automation",
                "Governance, Risk & Compliance", "Ethical Hacking Methodology", "Capture the Flag Practice",
                "Certification Prep (Security+) I", "Certification Prep (Security+) II", "Capstone & Interview Prep"
        };
        return build(
                "Cybersecurity Analyst",
                "A full year covering security fundamentals, networking, cryptography, defensive and "
                        + "offensive security, SOC operations, and incident response.",
                "Cybersecurity Analyst", weeks,
                List.of(
                        ms(12, "Security Foundations", "Networking, Linux, and cryptography basics."),
                        ms(26, "Defensive Security", "Network defense and secure coding."),
                        ms(33, "Offensive Security", "Penetration testing methodology and tools."),
                        ms(40, "SOC & Response", "SIEM, monitoring, and incident response."),
                        ms(52, "Certification Ready", "Prepared for Security+ with a capstone project.")),
                List.of(
                        pr("Home Security Lab", "A virtual lab for practicing attacks and defense."),
                        pr("Vulnerability Assessment", "Scan and report on a target environment."),
                        pr("Incident Response Playbook", "Detect and respond to a simulated breach.")),
                List.of(
                        res("OWASP Top 10", "https://owasp.org/www-project-top-ten/", "article"),
                        res("TryHackMe", "https://tryhackme.com/", "course"),
                        res("NIST Cybersecurity Framework", "https://www.nist.gov/cyberframework", "documentation"),
                        res("Cybrary Courses", "https://www.cybrary.it/", "course")));
    }

    private RoadmapTemplate machineLearningEngineer() {
        String[] weeks = {
                "Python for ML Engineering", "Software Engineering Best Practices",
                "Data Structures & Algorithms", "NumPy & Pandas", "Statistics & Probability",
                "Linear Algebra for ML", "Data Preprocessing", "Feature Engineering", "ML Fundamentals",
                "Supervised Learning: Regression", "Supervised Learning: Classification",
                "Tree-Based Models", "Ensemble Methods", "Model Evaluation", "Hyperparameter Tuning",
                "Unsupervised Learning", "Neural Networks Fundamentals", "Deep Learning with TensorFlow",
                "Deep Learning with PyTorch", "CNNs for Computer Vision", "RNNs & Sequence Models",
                "NLP Fundamentals", "Transformers", "Working with LLMs", "Model Interpretability",
                "Experiment Tracking (MLflow)", "Data Versioning (DVC)", "Building ML Pipelines",
                "Feature Stores", "Model Serving Fundamentals", "REST APIs with FastAPI",
                "Containerizing ML Models", "Kubernetes for ML", "CI/CD for ML", "Model Monitoring",
                "Data & Concept Drift", "A/B Testing ML Models", "Scaling ML Systems",
                "Distributed Training", "Cloud ML (AWS SageMaker)", "Cloud ML (GCP Vertex AI)",
                "Big Data with Spark", "Streaming ML", "Recommender Systems", "Time Series",
                "MLOps Best Practices", "Responsible AI", "ML System Design", "End-to-End Project I",
                "End-to-End Project II", "Portfolio Building", "Interview Prep"
        };
        return build(
                "Machine Learning Engineer",
                "An engineering-focused year: strong software foundations, machine learning and deep "
                        + "learning, and the MLOps skills to build, deploy, and operate ML systems at scale.",
                "Machine Learning Engineer", weeks,
                List.of(
                        ms(8, "Engineering Foundations", "Software practices, DS&A, and data tooling."),
                        ms(24, "ML & Deep Learning", "Classical ML through transformers and LLMs."),
                        ms(34, "ML in Production", "Serving, containerizing, and CI/CD for models."),
                        ms(46, "Scalable MLOps", "Monitoring, scaling, and distributed training."),
                        ms(52, "ML Engineer Portfolio", "Shipped end-to-end ML systems and interview prep.")),
                List.of(
                        pr("End-to-End ML Pipeline", "Training-to-serving pipeline with MLflow and Docker."),
                        pr("Model Serving API", "A FastAPI service serving a model on Kubernetes."),
                        pr("Monitored ML System", "Deployed model with drift detection and monitoring.")),
                List.of(
                        res("MLflow Documentation", "https://mlflow.org/docs/latest/index.html", "documentation"),
                        res("Made With ML", "https://madewithml.com/", "course"),
                        res("Google ML Crash Course", "https://developers.google.com/machine-learning/crash-course", "course"),
                        res("FastAPI Documentation", "https://fastapi.tiangolo.com/", "documentation")));
    }

    // ============================================================================================
    // Builders
    // ============================================================================================

    private RoadmapTemplate build(String title, String description, String careerPath, String[] weekTitles,
                                  List<Map<String, Object>> milestones,
                                  List<Map<String, Object>> projects,
                                  List<Map<String, Object>> resources) {
        return RoadmapTemplate.builder()
                .title(title)
                .description(description)
                .careerPath(careerPath)
                .durationWeeks(weekTitles.length)
                .weeklyPlan(toJson(buildWeeklyPlan(weekTitles)))
                .milestones(toJson(milestones))
                .recommendedProjects(toJson(projects))
                .learningResources(toJson(resources))
                .build();
    }

    private List<Map<String, Object>> buildWeeklyPlan(String[] weekTitles) {
        List<Map<String, Object>> weeks = new ArrayList<>();
        for (int i = 0; i < weekTitles.length; i++) {
            String title = weekTitles[i];
            List<Map<String, Object>> days = new ArrayList<>();
            for (int d = 0; d < DAY_PHASES.length; d++) {
                days.add(Map.of("day", d + 1, "topic", title + ": " + DAY_PHASES[d]));
            }
            weeks.add(Map.of("week", i + 1, "title", title, "days", days));
        }
        return weeks;
    }

    private Map<String, Object> ms(int week, String title, String description) {
        return Map.of("week", week, "title", title, "description", description);
    }

    private Map<String, Object> pr(String name, String description) {
        return Map.of("name", name, "description", description);
    }

    private Map<String, Object> res(String name, String url, String type) {
        return Map.of("name", name, "url", url, "type", type);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize roadmap seed data to JSON", ex);
        }
    }
}
