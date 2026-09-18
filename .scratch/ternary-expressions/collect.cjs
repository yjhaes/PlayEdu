const fs = require('fs');
const path = require('path');
const cp = require('child_process');
const root = path.resolve(__dirname, '../..');
const files = cp.execFileSync('rg', ['--files', 'playedu-api', '-g', '*.java', '-g', '!target', '-g', '!node_modules'], {cwd:root,encoding:'utf8'}).trim().split(/\r?\n/).sort();
const entries = [];
let scanned = 0;
for (const file of files) {
  const source = fs.readFileSync(path.join(root,file),'utf8');
  scanned++;
  const normalized = file.replace(/\\/g,'/');
  if (!file.endsWith('.java')) {
    const tree = ts.createSourceFile(file,source,ts.ScriptTarget.Latest,true);
    function visit(node) {
      if (ts.isConditionalExpression(node)) {
        const pos = tree.getLineAndCharacterOfPosition(node.questionToken.getStart(tree));
        entries.push({file:normalized,line:pos.line+1,column:pos.character+1,code:node.getText(tree),language:file.endsWith('x')?'tsx':'typescript'});
      }
      ts.forEachChild(node,visit);
    }
    visit(tree);
  } else {
    // Preserve positions while masking comments, strings, characters and Java text blocks.
    const masked = source.replace(/\/\*[\s\S]*?\*\/|\/\/[^\r\n]*|"""[\s\S]*?"""|"(?:\\[\s\S]|[^"\\])*"|'(?:\\[\s\S]|[^'\\])*'/g, value=>value.replace(/[^\r\n]/g,' '));
    for(let i=0;i<masked.length;i++) {
      if(masked[i]!=='?') continue;
      if(/^\s*(?:extends\b|super\b|>|,|\))/.test(masked.slice(i+1))) continue;
      const line = source.slice(0,i).split('\n').length;
      const lineStart = source.lastIndexOf('\n',i)+1;
      let start = lineStart;
      if(source.slice(start,i).trim()==='') start = source.lastIndexOf('\n',Math.max(0,start-2))+1;
      let end = masked.indexOf(';',i);
      if(end<0) end=source.indexOf('\n',i);
      const colon = masked.indexOf(':',i);
      if(colon<0 || (end>=0 && colon>end)) throw new Error(`Unmatched ternary: ${file}:${line}`);
      if(end<0) end=source.length-1;
      entries.push({file:normalized,line,column:i-lineStart+1,code:source.slice(start,end+1).trim(),language:'java'});
    }
  }
}
entries.sort((a,b)=>a.file.localeCompare(b.file)||a.line-b.line||a.column-b.column);
const groups = new Map();
for(const entry of entries) {
  if(!groups.has(entry.file)) groups.set(entry.file,[]);
  groups.get(entry.file).push(entry);
}
const modules = new Map();
for(const e of entries) {
  const parts=e.file.split('/');
  const module=parts[0]==='playedu-api'?parts.slice(0,2).join('/'):parts[0];
  if(!modules.has(module)) modules.set(module,{count:0,files:new Set()});
  modules.get(module).count++; modules.get(module).files.add(e.file);
}
let report='# 三元表达式收集清单\n\n';
report+='收集日期：2026-09-16。范围：playedu-api 下 rg 默认可见的后端 Java 文件，包含测试；排除依赖和构建产物。\n\n';
report+='屏蔽 Java 注释和字符串后识别问号，并排除泛型通配符及 SQL 占位符。每个三元运算符计一处，嵌套表达式分别计数；行号指向 `?`。代码片段为所在语句上下文。\n\n';
report+=`扫描 **${scanned}** 个文件，发现 **${entries.length}** 处三元表达式，涉及 **${groups.size}** 个文件。\n\n`;
report+='Java 实体 equals/hashCode 中的三元表达式也保留在清单中。\n\n';
report+='| 模块 | 表达式数 | 文件数 |\n| --- | ---: | ---: |\n';
for(const [name,data] of modules) report+=`| ${name} | ${data.count} | ${data.files.size} |\n`;
report+='\n## 文件索引\n\n| 文件 | 数量 | 问号行号（重复表示同一行多处） |\n| --- | ---: | --- |\n';
for(const [file,items] of groups) report+=`| [${file}](${root.replace(/\\/g,'/')}/${file}:${items[0].line}) | ${items.length} | ${items.map(e=>e.line).join(', ')} |\n`;
report+='\n## 代码明细\n';
for(const [file,items] of groups) {
  report+=`\n### ${file}\n`;
  for(const e of items) {
    const snippet=e.file.includes('/public/js/DPlayer.min.js')&&e.code.length>600?e.code.slice(0,600)+' /* … 第三方压缩代码片段截断，完整内容见 expressions.json … */':e.code;
    report+=`\n[第 ${e.line} 行，第 ${e.column} 列](${root.replace(/\\/g,'/')}/${file}:${e.line})\n\n\`\`\`${e.language}\n${snippet}\n\`\`\`\n`;
  }
}
fs.writeFileSync(path.join(__dirname,'README.md'),report);
fs.writeFileSync(path.join(__dirname,'expressions.json'),JSON.stringify({scanned,total:entries.length,fileCount:groups.size,entries},null,2)+'\n');
console.log(JSON.stringify({scanned,total:entries.length,fileCount:groups.size,modules:[...modules].map(([module,data])=>({module,count:data.count,files:data.files.size})),largestFiles:[...groups].map(([file,items])=>({file,count:items.length})).sort((a,b)=>b.count-a.count).slice(0,8)},null,2));
