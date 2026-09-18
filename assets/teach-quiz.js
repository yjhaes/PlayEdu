"use strict";

// Feedback is local to this page. It is not evidence of mastery until the learner reports it.
document.querySelectorAll("form[data-answer]").forEach((form) => {
  const input = form.querySelector("input");
  const feedback = form.querySelector(".feedback");
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const actual = input.value.trim().replace(/\s+/g, " ");
    const expected = form.dataset.answer.split("|");
    if (!actual) {
      feedback.textContent = "先写出你的判断，再检查。";
      feedback.dataset.state = "retry";
      return;
    }
    const correct = expected.includes(actual);
    feedback.textContent = correct ? form.dataset.success : form.dataset.hint;
    feedback.dataset.state = correct ? "correct" : "retry";
  });
});
