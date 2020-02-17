# Elasticsearch Playground

## Elastic

### Check Health
```zsh
curl -X GET "localhost:9200/_cat/nodes?v&pretty"  
```

### Load document
```zsh
curl -H "Content-Type: application/json" -XPOST "localhost:9200/bank/_bulk?pretty&refresh" --data-binary "@accounts.json"
```

### Check Indices
```zsh
curl "localhost:9200/_cat/indices?v" 
```