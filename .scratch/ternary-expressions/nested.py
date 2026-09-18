import json,re
from pathlib import Path

root=Path(__file__).resolve().parents[2]
data=json.loads((Path(__file__).parent/'expressions.json').read_text(encoding='utf8'))
results=[]
for name in sorted({e['file'] for e in data['entries']}):
    source=(root/name).read_text(encoding='utf8')
    masked=re.sub(r'/\*[\s\S]*?\*/|//[^\r\n]*|"""[\s\S]*?"""|"(?:\\[\s\S]|[^"\\])*"|\'(?:\\[\s\S]|[^\'\\])*\'',lambda m:re.sub(r'[^\r\n]',' ',m[0]),source)
    depth=0
    stack=[]
    for i,c in enumerate(masked):
        if c in '([{': depth+=1
        elif c in ')]}':
            while stack and stack[-1]['depth']>=depth: stack.pop()
            depth-=1
        elif c==';': stack=[]
        elif c==',':
            while stack and stack[-1]['depth']>=depth: stack.pop()
        elif c==':' and (i==0 or masked[i-1]!=':') and (i+1==len(masked) or masked[i+1]!=':'):
            while stack and stack[-1]['colon']: stack.pop()
            if stack: stack[-1]['colon']=True
        elif c=='?' and not re.match(r'\s*(extends\b|super\b|>|,|\))',masked[i+1:]):
            if stack:
                results.append({'file':name,'outerLine':source.count('\n',0,stack[-1]['pos'])+1,'innerLine':source.count('\n',0,i)+1})
            stack.append({'pos':i,'depth':depth,'colon':False})
print(json.dumps(results,ensure_ascii=False,indent=2))
