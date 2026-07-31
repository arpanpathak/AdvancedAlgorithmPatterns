/* ==========================================================================
   Coding Interview Fight Club — interactive book JS
   1. Groups adjacent code blocks into language tabs (Kotlin/Java/C++/Python/Rust)
   2. Adds a copy-to-clipboard button to every code block
   ========================================================================== */
(function () {
  'use strict';

  var LANG_LABELS = {
    kotlin: 'Kotlin',
    java: 'Java',
    cpp: 'C++',
    c: 'C',
    python: 'Python',
    rust: 'Rust',
    javascript: 'JS',
    typescript: 'TS',
    text: 'text',
    plaintext: 'text',
    bash: 'bash',
    sh: 'bash'
  };

  function langOf(pre) {
    var code = pre.querySelector('code');
    if (!code) return 'text';
    var m = (code.className || '').match(/language-([\w+-]+)/);
    var raw = m ? m[1] : 'text';
    return LANG_LABELS[raw] || raw;
  }

  function isCodePre(el) {
    return el && el.tagName === 'PRE' && el.querySelector('code');
  }

  /* Build tabs from a run of adjacent <pre> siblings. */
  function buildTabs(run) {
    var parent = run[0].parentNode;
    var container = document.createElement('div');
    container.className = 'code-tabs';

    var nav = document.createElement('div');
    nav.className = 'code-tabs-nav';
    run.forEach(function (pre, idx) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'code-tab-btn' + (idx === 0 ? ' active' : '');
      btn.textContent = langOf(pre);
      btn.addEventListener('click', function () {
        var buttons = nav.querySelectorAll('.code-tab-btn');
        buttons.forEach(function (b) { b.classList.remove('active'); });
        btn.classList.add('active');
        run.forEach(function (p, k) { p.style.display = k === idx ? 'block' : 'none'; });
      });
      nav.appendChild(btn);
    });
    container.appendChild(nav);

    var body = document.createElement('div');
    body.className = 'code-tabs-body';
    var copy = document.createElement('button');
    copy.type = 'button';
    copy.className = 'copy-btn';
    copy.textContent = 'Copy';
    copy.addEventListener('click', function () {
      var text = run.map(function (p) { return p.innerText; }).join('\n');
      copyCode(text, copy);
    });
    body.appendChild(copy);

    run.forEach(function (pre, idx) {
      pre.style.display = idx === 0 ? 'block' : 'none';
      body.appendChild(pre);
    });
    container.appendChild(body);

    parent.replaceChild(container, run[0]);
  }

  /* Wrap a lone code block with a copy button. */
  function wrapLone(pre) {
    var wrap = document.createElement('div');
    wrap.className = 'code-block-wrap';
    var copy = document.createElement('button');
    copy.type = 'button';
    copy.className = 'copy-btn';
    copy.textContent = 'Copy';
    copy.addEventListener('click', function () {
      copyCode(pre.innerText, copy);
    });
    pre.parentNode.insertBefore(wrap, pre);
    wrap.appendChild(copy);
    wrap.appendChild(pre);
  }

  function copyCode(text, btn) {
    if (!navigator.clipboard) return;
    navigator.clipboard.writeText(text).then(function () {
      var old = btn.textContent;
      btn.textContent = 'Copied!';
      setTimeout(function () { btn.textContent = old; }, 1200);
    });
  }

  function initTabs() {
    var pres = Array.prototype.slice.call(document.querySelectorAll('pre'));
    var i = 0;
    while (i < pres.length) {
      if (!isCodePre(pres[i])) { i++; continue; }
      var run = [pres[i]];
      var next = pres[i].nextElementSibling;
      while (isCodePre(next)) {
        run.push(next);
        next = next.nextElementSibling;
      }
      if (run.length > 1) {
        buildTabs(run);
      } else {
        wrapLone(run[0]);
      }
      i += run.length;
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initTabs);
  } else {
    initTabs();
  }
})();
