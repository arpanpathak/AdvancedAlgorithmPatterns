/* ==========================================================================
   Coding Interview Fight Club — interactive book JS
   1. Groups adjacent code blocks into language tabs (Kotlin/Java/C++/Python/Rust)
   2. Adds a copy-to-clipboard button to every code block

   IMPORTANT: never detach a <pre> from the document before the container is
   in place — a thrown DOM error would blank the page's code entirely.
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

  /* Build language tabs from a run of adjacent <pre> siblings.
     Only the ACTIVE language is visible (classic tabbed code). The first
     block (Kotlin) is active by default; clicking a language shows only
     that one; "All" reveals everything. If JS ever fails, a <noscript>
     fallback is not needed because we only HIDE via inline styles set by
     this script — blocks are never removed from the DOM. */
  function buildTabs(run) {
    var parent = run[0].parentNode;
    var container = document.createElement('div');
    container.className = 'code-tabs';

    var langs = run.map(langOf);

    var nav = document.createElement('div');
    nav.className = 'code-tabs-nav';

    var allBtn = document.createElement('button');
    allBtn.type = 'button';
    allBtn.className = 'code-tab-btn';
    allBtn.textContent = 'All';
    allBtn.addEventListener('click', function () {
      setActive(allBtn);
      run.forEach(function (p) { p.style.display = 'block'; });
    });
    nav.appendChild(allBtn);

    var uniqueLangs = langs.filter(function (l, i) { return langs.indexOf(l) === i; });
    var langBtns = {};
    uniqueLangs.forEach(function (l) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'code-tab-btn';
      btn.textContent = l;
      btn.addEventListener('click', function () {
        setActive(btn);
        run.forEach(function (p, k) { p.style.display = langs[k] === l ? 'block' : 'none'; });
      });
      langBtns[l] = btn;
      nav.appendChild(btn);
    });

    function setActive(btn) {
      nav.querySelectorAll('.code-tab-btn').forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
    }

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

    /* Order matters: put the container in the DOM FIRST (while run[0] is still
       attached to parent), then move the pre blocks into it. */
    parent.replaceChild(container, run[0]);
    run.forEach(function (pre, idx) {
      pre.style.display = idx === 0 ? 'block' : 'none';   // tabbed: first language active
      body.appendChild(pre);
    });
    container.appendChild(body);

    // Default active tab = the first language (Kotlin).
    var first = uniqueLangs[0] || null;
    if (first && langBtns[first]) {
      setActive(langBtns[first]);
    }
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
      try {
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
      } catch (e) {
        /* Never let one bad block blank the page: leave remaining blocks as-is. */
        i++;
      }
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initTabs);
  } else {
    initTabs();
  }
})();

/* ---- MathJax guarantee ----
   mdBook injects MathJax async; the config script in head.hbs can race it.
   After the page settles, force a fresh PreProcess + Typeset pass so every
   $...$ / $$...$$ equation renders. tex2jax skips pre/code by default, so
   code blocks are never touched. */
(function () {
  function forceMathJax() {
    if (window.MathJax && MathJax.Hub) {
      MathJax.Hub.Config({
        tex2jax: {
          inlineMath: [['$', '$'], ['\\(', '\\)']],
          displayMath: [['$$', '$$'], ['\\[', '\\]']],
          processEscapes: true
        }
      });
      MathJax.Hub.Queue(['PreProcess', MathJax.Hub]);
      MathJax.Hub.Queue(['Typeset', MathJax.Hub]);
    }
  }
  if (document.readyState === 'complete') {
    forceMathJax();
  } else {
    window.addEventListener('load', forceMathJax);
  }
})();
