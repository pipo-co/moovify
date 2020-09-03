window.addEventListener('load', function(){

    configureEasyMDE();

    let formElem = document.getElementById('new-post-form');
    let addMovieInputElem = document.getElementById('add-movie-input');
    let addMovieButtonElem = document.getElementById('add-movie-button');
    let moviesSelectedElem = document.getElementById('movies-selected');
    let submitFormButton = document.getElementById('submit-form-button');
    let datalistElem = document.getElementById('movie-list');
    let openModalButtonElem = document.getElementById('open-modal-button');
    let moviesModalElem = document.getElementById('movies-modal');

    // Validate form before opening modal
    // reportValidity is not compatible with IE, Chrome < 40, Firefox < 49, Edge < 17;
    openModalButtonElem.addEventListener('click', () => {
        if(formElem.reportValidity())
            UIkit.modal(moviesModalElem).show();
    }, false);

    addMovieButtonElem.addEventListener('click',
        () => addMovie(formElem, addMovieInputElem, datalistElem, moviesSelectedElem),
        false);

    submitFormButton.addEventListener('click', () => formElem.submit(), false);

    moviesModalElem.addEventListener('beforehide', () => cancelModal(formElem, datalistElem, moviesSelectedElem), false);

}, false);

function configureEasyMDE(){
    new EasyMDE({
        element: document.getElementById("create-post-data"),
        spellChecker: false,
        autosave: {
            enabled: true,
            uniqueId: "source",
            delay: 1000,
            text: "Saved: ",
        },
        forceSync: true,
        initialValue: "El valor inicial del editor. Cuando se carga la pagina, si no hay nada guardado, se llena con esto",
        minHeight: "300px", // This is the default minHeight
        parsingConfig: {
            allowAtxHeaderWithoutSpace: true,
            strikethrough: true,
            underscoresBreakWords: true
        },
        placeholder: "This is the Placeholder. Solo aparece si no hay texto.",

        // Upload Image Support Configurations

        inputStyle: "textarea", // Could be contenteditable
        theme: "easymde", // Default

        toolbar: ["bold", "italic", "heading", "heading-smaller", "heading-bigger", "|",
            "quote", "unordered-list", "ordered-list", "|",
            "horizontal-rule", "strikethrough",
            "link", "image", "|",
            "preview", "side-by-side", "fullscreen", "|",
            "clean-block", "guide",
        ],

        renderingConfig: {
            sanitizerFunction: (dirtyHTML) => DOMPurify.sanitize(dirtyHTML),
        }

    });
}

function addMovie(formElem, inputElem, datalistElem, moviesSelectedElem){
    let movieName = inputElem.value;
    if(!movieName)
        return;

    let movieOption = datalistElem.querySelector(`option[value = '${movieName}']`);

    if(!movieOption)
        return;

    let movieId = movieOption.dataset.id;

    let newInput = document.createElement("input");
    newInput.setAttribute('name', `movies[]`);
    newInput.setAttribute('type', 'number');
    newInput.setAttribute('value', movieId);
    newInput.setAttribute('data-movie-name', movieName);
    newInput.style.display = 'none';

    // Add new movie to form
    formElem.appendChild(newInput);

    // Remove movie from selectable options
    datalistElem.removeChild(movieOption);

    // Clear movie input
    inputElem.value = "";

    // Add movie to movies selected list
    moviesSelectedElem
        .appendChild(document.createElement("p")
            .appendChild(document.createTextNode(movieName)));
}

function cancelModal(formElem, datalistElem, moviesSelectedElem) {

    formElem.querySelectorAll("input[name ^= 'movies']")
        .forEach(movieNode => {

            let opt = document.createElement("option");
            opt.setAttribute('value', movieNode.dataset.movieName);
            opt.setAttribute('data-id', movieNode.value);
            datalistElem.appendChild(opt);

            formElem.removeChild(movieNode);
        });

    moviesSelectedElem.innerHTML = "";
}
