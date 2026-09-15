import React, { useState } from "react";

interface Props {
  shortCode: string;
  shortUrl: string;
  originalUrl: string;
}

const UrlResult: React.FC<Props> = ({ shortCode, shortUrl, originalUrl }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(shortUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div style={styles.card}>
      <h2 style={styles.heading}>✅ URL Shortened!</h2>

      <div style={styles.row}>
        <span style={styles.label}>Short URL:</span>
        
        <a  href={shortUrl}
          target="_blank"
          rel="noreferrer"
          style={styles.link}
        >
          {shortUrl}
        </a>
        <button onClick={handleCopy} style={styles.copyBtn}>
          {copied ? "✅ Copied!" : "📋 Copy"}
        </button>
      </div>

      <div style={styles.row}>
        <span style={styles.label}>Original:</span>
        <span style={styles.original}>{originalUrl}</span>
      </div>

      <div style={styles.row}>
        <span style={styles.label}>Short Code:</span>
        <span style={styles.code}>{shortCode}</span>
      </div>
    </div>
  );
};

const styles: { [key: string]: React.CSSProperties } = {
  card: {
    backgroundColor: "#f7fafc",
    border: "2px solid #e2e8f0",
    borderRadius: "12px",
    padding: "28px",
    marginTop: "32px",
    maxWidth: "600px",
    margin: "32px auto 0",
    textAlign: "left",
  },
  heading: {
    color: "#276749",
    marginBottom: "20px",
    textAlign: "center",
  },
  row: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "16px",
    flexWrap: "wrap",
  },
  label: {
    fontWeight: "bold",
    color: "#4a5568",
    minWidth: "90px",
  },
  link: {
    color: "#4f46e5",
    textDecoration: "none",
    fontWeight: "600",
  },
  copyBtn: {
    padding: "6px 14px",
    backgroundColor: "#4f46e5",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  original: {
    color: "#718096",
    wordBreak: "break-all",
    fontSize: "14px",
  },
  code: {
    backgroundColor: "#edf2f7",
    padding: "4px 10px",
    borderRadius: "4px",
    fontFamily: "monospace",
    fontWeight: "bold",
    color: "#2d3748",
  },
};

export default UrlResult;